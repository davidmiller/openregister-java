package uk.gov.register.core.external;

import uk.gov.register.core.Entry;
import uk.gov.register.core.Item;
import uk.gov.register.core.Record;
import uk.gov.register.db.EntryStore;

import java.time.Instant;
import java.util.Arrays;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

public class AppendEntryCommandHandler implements CommandHandler {
    private EntryStore entryStore;
    private String register;

    public AppendEntryCommandHandler(EntryStore entryStore, String register) {
        this.entryStore = entryStore;
        this.register = register;
    }

    @Override
    public void handle(RegisterCommand command) {
        AtomicInteger currentEntryNumber = new AtomicInteger(entryStore.entryDAO.currentEntryNumber());
        String sha256 = command.getData().get("item-hash").replace("sha-256:", "");
        Instant entryTimestamp = Instant.parse(command.getData().get("entry-timestamp"));
        Entry newEntry = new Entry(currentEntryNumber.incrementAndGet(), sha256, entryTimestamp);

        entryStore.entryDAO.insertInBatch(Arrays.asList(newEntry));
        Optional<Item> item = entryStore.itemDAO.getItemBySHA256(sha256);

        Record record = new Record(newEntry, item.get());
        entryStore.destinationDBUpdateDAO.upsertInCurrentKeysTable(register, Arrays.asList(record));

        entryStore.entryDAO.setEntryNumber(currentEntryNumber.get());
    }

    @Override
    public String getHandlerType() {
        return String.valueOf(CommandType.APPEND_ENTRY);
    }
}
