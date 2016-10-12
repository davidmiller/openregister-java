package uk.gov.register.util;

import org.skife.jdbi.v2.Handle;
import uk.gov.register.core.Entry;
import uk.gov.register.core.EntryLog;
import uk.gov.register.core.Register;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class AppendEntryCommandHandler implements CommandHandler {
    private final CommandHelper commandHelper;
    private final EntryLog entryLog;
    private final Register register;

    @Inject
    public AppendEntryCommandHandler(Register register, EntryLog entryLog) {
        this.register = register;
        this.entryLog = entryLog;
        this.commandHelper = new CommandHelper();
    }

    @Override
    public boolean execute(Handle handle, Iterable<RegisterCommand> commands) {
        List<Entry> stagedEntries = new ArrayList<>();

        commands.forEach(command -> {
            int entryNumber = register.getTotalEntries();
            Entry entry = commandHelper.getEntry(command, ++entryNumber);

            stagedEntries.add(entry);
        });

        entryLog.appendEntries(handle, stagedEntries);

        return true;
    }

    @Override
    public String getCommandName() {
        return "append-entry";
    }
}
