package uk.gov.register.core.external;


public interface ICommandExecutor {
    CommandResult execute(RegisterCommand command);
}

