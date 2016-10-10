package uk.gov.register.util;

import uk.gov.register.core.Item;
import uk.gov.register.core.Register;

import javax.inject.Inject;

public class AddItemCommandHandler implements CommandHandler {
    private final Register register;
    private final CommandHelper commandHelper;

    @Inject
    public AddItemCommandHandler(Register register, CommandHelper commandHelper) {
        this.register = register;
        this.commandHelper = commandHelper;
    }

    @Override
    public void execute(RegisterCommand command) {
        Item item = commandHelper.getItem(command);
        register.addItem(item);
    }

    @Override
    public String getCommandName() {
        return "add-item";
    }
}
