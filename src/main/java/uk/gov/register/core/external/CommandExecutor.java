package uk.gov.register.core.external;

import org.skife.jdbi.v2.Handle;
import uk.gov.register.db.EntryStore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CommandExecutor implements ICommandExecutor {

    private final Handle handle;
    private Map<String, CommandHandler> handlers = new HashMap<>();
    private EntryStore entryStore;

    public CommandExecutor(EntryStore entryStore) {
        this.entryStore = entryStore;
        this.handle = entryStore.handle;
    }

    public void register(CommandHandler commandHandler) {
        handlers.put(commandHandler.getHandlerType(), commandHandler);
    }

    @Override
    public List<CommandResult> execute(List<RegisterCommand> commands) {
        List<CommandResult> results = new ArrayList<>();
        handle.begin();
        Boolean shouldCommit = true;
        for (RegisterCommand command : commands) {
            CommandResult commandResult = execute(command);
//            log
            System.out.println(String.format("%s - %s with result: %s", commandResult.getCommandStatus().name(), command.getCommandType(), commandResult.getMessage()));
            results.add(commandResult);
            if (commandResult.getCommandStatus() == CommandStatus.Failed) {
                shouldCommit = false;
                break;
            }
        }

        if (shouldCommit) {
            handle.commit();
        } else {
            handle.rollback();
        }

        return results;
    }


    @Override
    public CommandResult execute(RegisterCommand command) {
        CommandHandler handler = getCommandHandlerFor(command.getCommandType());
        CommandResult result;

        try {
            handler.handle(command);
            // check handler messages??
            // add them to command results?
            // create commands?
            result = CommandResult.Executed();

        } catch (Exception e) {
            result = CommandResult.Failed(e.getMessage());
//            throw e;
        }
        return result;
    }


    private CommandHandler getCommandHandlerFor(String commandType) {
        return handlers.get(commandType);
    }
}
