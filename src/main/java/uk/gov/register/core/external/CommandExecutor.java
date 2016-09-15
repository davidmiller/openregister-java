package uk.gov.register.core.external;

import org.skife.jdbi.v2.Handle;
import uk.gov.register.core.external.handlers.AssertFieldCommandHandler;
import uk.gov.register.db.EntryStore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommandExecutor implements ICommandExecutor {

    private final Handle handle;
    private Map<String, CommandHandler> commandHandlers = new HashMap<>();
    private Map<String, AssertionHandler> assertionHandlers = new HashMap<>();


    private EntryStore entryStore;

    public CommandExecutor(EntryStore entryStore) {
        this.entryStore = entryStore;
        this.handle = entryStore.getHandle();
//        pass in event listener?
    }
    public void register(AssertionHandler assertionHandler){
        assertionHandlers.put(assertionHandler.getHandlerType(), assertionHandler);
    }
    public void register(CommandHandler commandHandler) {
        commandHandlers.put(commandHandler.getHandlerType(), commandHandler);
    }

    @Override
    public List<ExecutionResult> execute(List<RegisterCommand> commands) {
        Map<String, List<String>> activeAssertions = new HashMap();
        register(new AssertFieldCommandHandler(activeAssertions));

        List<ExecutionResult> results = new ArrayList<>();
        handle.begin();
        Boolean shouldCommit = true;
        for (RegisterCommand command : commands) {
            ExecutionResult executionResult;

            executionResult = validate(command, activeAssertions);
            if(executionResult.getCommandStatus() == CommandStatus.Executed) {
                executionResult = execute(command);
            }
//            log
            System.out.println(String.format("%s - %s with message: %s", executionResult.getCommandStatus().name(), command.getCommandType(), executionResult.getMessage()));
            results.add(executionResult);
            if (executionResult.getCommandStatus() == CommandStatus.Failed) {
                shouldCommit = false;
                break;
            }
        }

        if (shouldCommit) {
            handle.commit();
            System.out.println("Ahh goodie, commit");
        } else {
            System.out.println("Rolling back - no changes made to DB");
            handle.rollback();
        }

        return results;
    }

    private ExecutionResult validate(RegisterCommand command, Map<String, List<String>> activeAssertions) {
        ExecutionResult result = ExecutionResult.Executed();

        for (Map.Entry<String, List<String>> entry : activeAssertions.entrySet()){
            if(command.getData().containsKey(entry.getKey())){
                for(String assertionName : entry.getValue()){

                    AssertionHandler asHandler = getAssertionHandlerFor(assertionName);
                    result = asHandler.validate(command.getData().get(entry.getKey()));
                    if(result.getCommandStatus() == CommandStatus.Failed){
                        break;
                    }
                }
            }
        }


        return result;

    }


    @Override
    public ExecutionResult execute(RegisterCommand command) {
        CommandHandler handler = getCommandHandlerFor(command.getCommandType());
        ExecutionResult result;

        try {
            result = handler.handle(command);
            // issue/publish an event?
            // check handler messages??
            // add them to command results?
            // create commands?
        } catch (Exception e) {
            result = ExecutionResult.Failed(e.getMessage());
        }
        return result;
    }


    private CommandHandler getCommandHandlerFor(String commandType) {
        return commandHandlers.get(commandType);
    }

    private AssertionHandler getAssertionHandlerFor(String assertionName) {
        return assertionHandlers.get(assertionName);
    }
}
