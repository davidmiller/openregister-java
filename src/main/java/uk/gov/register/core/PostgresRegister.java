package uk.gov.register.core;

import com.google.common.collect.Lists;
import org.glassfish.hk2.api.IterableProvider;
import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.Handle;
import org.skife.jdbi.v2.TransactionIsolationLevel;
import org.skife.jdbi.v2.exceptions.UnableToCloseResourceException;
import uk.gov.register.configuration.RegisterNameConfiguration;
import uk.gov.register.db.RecordIndex;
import uk.gov.register.exceptions.ItemMissingFromRegisterException;
import uk.gov.register.util.CommandHandler;
import uk.gov.register.util.RegisterCommand;
import uk.gov.register.views.ConsistencyProof;
import uk.gov.register.views.EntryProof;
import uk.gov.register.views.RegisterProof;

import javax.inject.Inject;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class PostgresRegister implements Register {
    private final RecordIndex recordIndex;

    private final String registerName;
    private final EntryLog entryLog;
    private final ItemStore itemStore;
    private final DBI dbi;
    private final List<Entry> stagedEntries;
    private final Set<Item> stagedItems;
    private Map<String, CommandHandler> commandHandlerLookup;
    private Handle registerHandle;
    private final IterableProvider<CommandHandler> commandHandlers;

    @Inject
    public PostgresRegister(RegisterNameConfiguration registerNameConfig,
                            RecordIndex recordIndex,
                            EntryLog entryLog,
                            ItemStore itemStore,
                            DBI dbi,
                            IterableProvider<CommandHandler> commandHandlers) {
        this.recordIndex = recordIndex;
        registerName = registerNameConfig.getRegister();
        this.entryLog = entryLog;
        this.itemStore = itemStore;
        this.dbi = dbi;
        this.stagedEntries = new ArrayList<>();
        this.stagedItems = new HashSet<>();
        this.commandHandlers = commandHandlers;

        Stream<CommandHandler> stream = StreamSupport.stream(commandHandlers.spliterator(), false);
        this.commandHandlerLookup = stream.collect(Collectors.toMap(CommandHandler::getCommandName, h -> h));

    }

    @Override
    public void loadSerializationFormatCommands(List<RegisterCommand> registerCommands) {
        if (registerCommands == null || registerCommands.isEmpty()) {
            return;
        }

        Map<String, List<RegisterCommand>> commands = registerCommands.stream()
            .collect(Collectors.groupingBy(
                RegisterCommand::getCommandName,
                Collectors.mapping(r -> r, Collectors.toList())));

        Handle handle = startTransaction(this.registerHandle);

        try {
            commandHandlers.forEach(handler -> {
                List<RegisterCommand> handlerCommands = commands.get(handler.getCommandName());

                if (!handlerCommands.isEmpty()) {
                    boolean result = handler.execute(handle, handlerCommands);

                    if (!result) {
                        handle.rollback();
                        throw new RuntimeException("Command failed, rolling back.");
                    }
                }
            });

            handle.commit();
        }
        finally {
            handle.close();
        }
    }

    @Override
    public void mintItems(Iterable<Item> items) {
        dbi.useTransaction(TransactionIsolationLevel.SERIALIZABLE, (handle, status) -> {
            itemStore.putItems(handle, items);
            AtomicInteger currentEntryNumber = new AtomicInteger(entryLog.getTotalEntries(handle));
            List<Record> records = StreamSupport.stream(items.spliterator(), false)
                    .map(item -> new Record(new Entry(currentEntryNumber.incrementAndGet(), item.getSha256hex(), Instant.now()), item))
                    .collect(Collectors.toList());
            entryLog.appendEntries(handle, Lists.transform(records, r -> r.entry));
            recordIndex.updateRecordIndex(handle, registerName, records);
        });
    }

    @Override
    public void addEntry(Entry entry) {
        if (entry == null) {
            return;
        }

        stagedEntries.add(entry);
    }

    @Override
    public void addItem(Item item) {
        if (item == null) {
            return;
        }

        stagedItems.add(item);
    }

    @Override
    public Optional<Entry> getEntry(int entryNumber) {
        return dbi.withHandle(handle -> entryLog.getEntry(handle, entryNumber));
    }

    @Override
    public Optional<Item> getItemBySha256(String sha256hex) {
        return dbi.withHandle(h -> itemStore.getItemBySha256(h, sha256hex));
    }

    @Override
    public int getTotalEntries() {
        int committedEntryCount = dbi.withHandle(entryLog::getTotalEntries);
        int stagedEntryCount = stagedEntries.size();

        return committedEntryCount + stagedEntryCount;
    }

    @Override
    public Collection<Entry> getEntries(int start, int limit) {
        return dbi.withHandle(h -> entryLog.getEntries(h, start, limit));
    }

    @Override
    public Optional<Record> getRecord(String key) {
        return dbi.withHandle(h -> recordIndex.getRecord(h, key));
    }

    @Override
    public Collection<Entry> allEntriesOfRecord(String key) {
        return dbi.withHandle(h -> recordIndex.findAllEntriesOfRecordBy(h, registerName, key));
    }

    @Override
    public List<Record> getRecords(int limit, int offset) {
        return dbi.withHandle(h -> recordIndex.getRecords(h, limit, offset));
    }

    @Override
    public Collection<Entry> getAllEntries() {
        return dbi.withHandle(entryLog::getAllEntries);
    }

    @Override
    public Collection<Item> getAllItems() {
        return dbi.withHandle(itemStore::getAllItems);
    }

    @Override
    public int getTotalRecords() {
        return dbi.withHandle(recordIndex::getTotalRecords);
    }

    @Override
    public Optional<Instant> getLastUpdatedTime() {
        return dbi.withHandle(entryLog::getLastUpdatedTime);
    }

    @Override
    public List<Record> max100RecordsFacetedByKeyValue(String key, String value) {
        return dbi.withHandle(h -> recordIndex.findMax100RecordsByKeyValue(h, key, value));
    }

    @Override
    public RegisterProof getRegisterProof() throws NoSuchAlgorithmException {
        Handle handle = startTransaction(this.registerHandle);

//        mintStagedData(handle);

        return entryLog.getRegisterProof(handle);
    }

    @Override
    public EntryProof getEntryProof(int entryNumber, int totalEntries) {
        return dbi.inTransaction((handle, status) ->
                entryLog.getEntryProof(handle, entryNumber, totalEntries));
    }

    @Override
    public ConsistencyProof getConsistencyProof(int totalEntries1, int totalEntries2) {
        return dbi.inTransaction((handle, status) ->
                entryLog.getConsistencyProof(handle, totalEntries1, totalEntries2));
    }

    private Handle startTransaction(Handle handle) {
        try {
            if (handle == null) {
                handle = dbi.open();
                handle.setTransactionIsolation(TransactionIsolationLevel.SERIALIZABLE);
            }

            if (handle.isInTransaction()) {
                handle.close();
            }

            handle.begin();

            return handle;
        } catch (UnableToCloseResourceException ex) {
            return null;
        }
    }

    private void mintStagedData(Handle handle) {
        if (stagedItems.isEmpty() && stagedEntries.isEmpty()) {
            return;
        }

        itemStore.putItems(handle, stagedItems);

        // item-hash to item
        Map<String, Item> itemsByHash = stagedItems.stream()
                .collect(Collectors.toMap(Item::getSha256hex, i -> i));

        List<Record> records = stagedEntries.stream()
                .map(entry -> {
                    String hash = entry.getSha256hex();
                    Item item;

                    if (itemsByHash.containsKey(hash)) {
                        item = itemsByHash.get(hash);
                    } else {
                        try {
                            item = itemStore.getItemBySha256(handle, hash).get();
                        } catch (NoSuchElementException ex) {
                            throw new ItemMissingFromRegisterException(hash);
                        }
                    }

                    return new Record(entry, item);
                })
                .collect(Collectors.toList());

        entryLog.appendEntries(handle, Lists.transform(records, r -> r.entry));
        recordIndex.updateRecordIndex(handle, registerName, records);

        // Flush the staging data
        stagedEntries.clear();
        stagedItems.clear();
    }
}
