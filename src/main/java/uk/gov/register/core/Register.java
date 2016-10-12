package uk.gov.register.core;

import org.jvnet.hk2.annotations.Contract;
import uk.gov.register.util.RegisterCommand;

import java.util.List;

@Contract
public interface Register extends RegisterReadOnly {
    void loadSerializationFormatCommands(List<RegisterCommand> commands);

    void mintItems(Iterable<Item> items);

    void addEntry(Entry entry);

    void addItem(Item item);
}
