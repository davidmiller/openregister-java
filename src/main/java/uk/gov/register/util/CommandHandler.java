package uk.gov.register.util;

import org.skife.jdbi.v2.Handle;

public interface CommandHandler {
    boolean execute(Handle handle, Iterable<RegisterCommand> commands);
    String getCommandName();
}