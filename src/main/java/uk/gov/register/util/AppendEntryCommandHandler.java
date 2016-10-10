package uk.gov.register.util;

import uk.gov.register.core.Entry;
import uk.gov.register.core.Register;

import javax.inject.Inject;

public class AppendEntryCommandHandler implements CommandHandler {
    private final CommandHelper commandHelper;
    private final Register register;

    @Inject
    public AppendEntryCommandHandler(CommandHelper commandHelper, Register register) {
        this.commandHelper = commandHelper;
        this.register = register;
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