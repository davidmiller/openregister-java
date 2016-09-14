package uk.gov.register.core.external;


import java.util.List;

public interface ICommandExecutor {
    CommandResult execute(RegisterCommand command);
    List<CommandResult> execute(List<RegisterCommand> commands);
    void register(CommandHandler commandHandler);
}

