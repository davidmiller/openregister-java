package uk.gov.register.core.external;


//public abstract class CommandHandler<T extends RegisterCommand> {
//    private final Class<T> commandType;
//
//    public CommandHandler(Class<T> commandType) {
//        this.commandType = commandType;
//    }
//
//    abstract void dbHandle(T command);
//
//    public final Class<T> getCommandType() {
//        return commandType;
//    }
//
//}

public interface CommandHandler {
    ExecutionResult handle(RegisterCommand command);
    String getHandlerType();
}
