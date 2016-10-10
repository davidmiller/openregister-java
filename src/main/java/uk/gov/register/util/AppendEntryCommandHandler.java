package uk.gov.register.util;

import uk.gov.register.core.Entry;
import uk.gov.register.core.Register;

import javax.inject.Inject;

public class AppendEntryCommandHandler implements CommandHandler {
    private final CommandHelper commandHelper;
    private final Register register;

    @Inject
    public AppendEntryCommandHandler(Register register) {
        this.register = register;
        this.commandHelper = new CommandHelper();
    }

    @Override
    public void execute(RegisterCommand command) {
        int entryNumber = register.getTotalEntries();
        Entry entry = commandHelper.getEntry(command, ++entryNumber);

        register.addEntry(entry);
    }

    @Override
    public String getCommandName() {
        return "append-entry";
    }
}
