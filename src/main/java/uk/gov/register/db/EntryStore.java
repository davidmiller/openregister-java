package uk.gov.register.db;

import com.fasterxml.jackson.databind.JsonNode;
import org.skife.jdbi.v2.Handle;
import org.skife.jdbi.v2.TransactionIsolationLevel;
import org.skife.jdbi.v2.sqlobject.Transaction;
import org.skife.jdbi.v2.sqlobject.mixins.GetHandle;
import uk.gov.register.core.Entry;
import uk.gov.register.core.Item;
import uk.gov.register.core.Record;
import uk.gov.register.core.external.CommandResult;
import uk.gov.register.core.external.ICommandExecutor;
import uk.gov.register.core.external.RegisterCommand;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public abstract class EntryStore implements GetHandle {
    public final EntryQueryDAO entryDAO;
    public final ItemDAO itemDAO;
    public final DestinationDBUpdateDAO destinationDBUpdateDAO;
    private ICommandExecutor commandExecutor;

    public EntryStore() {
        Handle handle = getHandle();
        this.entryDAO = handle.attach(EntryQueryDAO.class);
        this.itemDAO = handle.attach(ItemDAO.class);
        this.destinationDBUpdateDAO = handle.attach(DestinationDBUpdateDAO.class);
        this.entryDAO.ensureSchema();
        this.itemDAO.ensureSchema();
    }

    @Transaction(TransactionIsolationLevel.SERIALIZABLE)
    public void load(String registerName, Iterable<JsonNode> itemNodes) {
        AtomicInteger currentEntryNumber = new AtomicInteger(entryDAO.currentEntryNumber());
        List<Item> items = StreamSupport.stream(itemNodes.spliterator(), false)
                .map(Item::new)
                .collect(Collectors.toList());
        List<Record> records = items.stream()
                .map(item -> new Record(new Entry(currentEntryNumber.incrementAndGet(), item.getSha256hex(), Instant.now()), item))
                .collect(Collectors.toList());
        List<Entry> entries = records.stream()
                .map(record -> record.entry)
                .collect(Collectors.toList());

        entryDAO.insertInBatch(entries);
        itemDAO.insertInBatch(items);
        destinationDBUpdateDAO.upsertInCurrentKeysTable(registerName, records);

        entryDAO.setEntryNumber(currentEntryNumber.get());
    }

    @Transaction(TransactionIsolationLevel.SERIALIZABLE)
    public void load2(String registerName, Iterable<RegisterCommand> commands) {

        commands.forEach(command -> {
            CommandResult commandResult = commandExecutor.execute(command);
            System.out.println(String.format("%s - %s with result: %s", commandResult.getCommandStatus().name(), command.getClass().getName(), commandResult.getMessage()));

        });

    }


    public void setCommandExecutor(ICommandExecutor commandExecutor) {
        this.commandExecutor = commandExecutor;
    }
}

