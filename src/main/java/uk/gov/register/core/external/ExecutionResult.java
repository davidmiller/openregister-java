package uk.gov.register.core.external;

public class ExecutionResult extends Result<CommandStatus> {

    public ExecutionResult(CommandStatus commandStatus, String message) {
        super(commandStatus, message);
    }

    public static ExecutionResult Executed() {
        return ExecutionResult.Executed("success");
    }
    public static ExecutionResult Executed(String message) {
        return new ExecutionResult(CommandStatus.Executed, message);
    }
    public static ExecutionResult Failed(String message) {
        return new ExecutionResult(CommandStatus.Failed, message);
    }

}

