package uk.gov.register.core.external;

/**
 * Created by christopherd on 13/09/2016.
 */
public abstract class CommandHandler<T extends RegisterCommand> {
    private final Class<T> commandType;

    public CommandHandler(Class<T> commandType) {
        this.commandType = commandType;
    }

    abstract void handle(T command);

    public final Class<T> getCommandType() {
        return commandType;
    }

}
