package uk.gov.register.core.external;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by christopherd on 13/09/2016.
 */
public class CommandExecutor implements ICommandExecutor {

    private Map<Class, CommandHandler> handlers = new HashMap<>();

    public void register(CommandHandler commandHandler) {
        handlers.put(commandHandler.getCommandType(), commandHandler);
    }

    @Override
    public CommandResult execute(RegisterCommand command) {
        CommandHandler handler = getCommandHandlerFor(command.getClass());
        CommandResult result;

        try {
            handler.handle(command);
            //check errors?
            result = CommandResult.Executed();

        } catch (Exception e) {
            result = CommandResult.Failed(e.getMessage());
        }
        return result;
    }

    private CommandHandler getCommandHandlerFor(Class commandType) {
        return handlers.get(commandType);
    }
}
