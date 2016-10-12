package uk.gov.register.util;

import org.skife.jdbi.v2.Handle;
import uk.gov.register.core.Item;
import uk.gov.register.core.ItemStore;
import uk.gov.register.core.Register;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class AddItemsCommandHandler implements CommandHandler {
    private final Register register;
    private final ItemStore itemStore;
    private final CommandHelper commandHelper;

    @Inject
    public AddItemsCommandHandler(Register register, ItemStore itemStore) {
        this.register = register;
        this.itemStore = itemStore;
        this.commandHelper = new CommandHelper();
    }

    @Override
    public boolean execute(Handle handle, Iterable<RegisterCommand> commands) {
        Set<Item> stagedItems = new TreeSet<>();

        commands.forEach(command -> {
            Item item = commandHelper.getItem(command);
            stagedItems.add(item);
        });

        itemStore.putItems(handle, stagedItems);

        return true;
    }

    @Override
    public String getCommandName() {
        return "add-item";
    }
}
