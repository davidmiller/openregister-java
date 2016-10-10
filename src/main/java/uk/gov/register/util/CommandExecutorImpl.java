package uk.gov.register.util;

import org.glassfish.hk2.api.IterableProvider;

import javax.inject.Inject;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class CommandExecutorImpl implements CommandExecutor {
    private Map<String, CommandHandler> commandHandlerLookup;

    @Inject
    public CommandExecutorImpl(IterableProvider<CommandHandler> commandHandlers) {
        Stream<CommandHandler> stream = StreamSupport.stream(commandHandlers.spliterator(), false);
        this.commandHandlerLookup = stream.collect(Collectors.toMap(CommandHandler::getCommandName, h -> h));
    }

    @Override
    public void execute(RegisterCommand command) {
        String commandName = command.getCommandName();
        if (this.commandHandlerLookup.containsKey(commandName)) {
            CommandHandler handler = this.commandHandlerLookup.get(commandName);
            handler.execute(command);
        }
    }
}