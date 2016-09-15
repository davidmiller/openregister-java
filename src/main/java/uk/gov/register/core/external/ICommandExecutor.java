package uk.gov.register.core.external;


import java.util.List;

public interface ICommandExecutor {
    ExecutionResult execute(RegisterCommand command);
    List<ExecutionResult> execute(List<RegisterCommand> commands);
    void register(CommandHandler commandHandler);
    void register(AssertionHandler assertionHandler);
}

