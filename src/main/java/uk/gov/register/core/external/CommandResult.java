package uk.gov.register.core.external;

/**
 * Created by christopherd on 13/09/2016.
 */
public class CommandResult {
    private final CommandStatus commandStatus;
    private final String message;

    public CommandResult(CommandStatus commandStatus, String message) {
        this.commandStatus = commandStatus;
        this.message = message;
    }

    public static CommandResult Executed() {
        return new CommandResult(CommandStatus.Executed, "success");
    }

    public static CommandResult Failed(String message) {
        return new CommandResult(CommandStatus.Failed, message);
    }

    public CommandStatus getCommandStatus() {
        return commandStatus;
    }

    public String getMessage() {
        return message;
    }
}
