package uk.gov.register.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CommandParserImpl implements CommandParser {
    @Override
    public List<RegisterCommand> parseCommands(String commandStr) {
        List<RegisterCommand> registerCommands = new ArrayList<>();
        String[] commands = commandStr.split("\n");

        Arrays.asList(commands).forEach(c -> {
            String[] allParams = c.split("\t");
            if (allParams.length < 2) {
                throw new RuntimeException("Command name and parameters must be specified");
            }

            String commandName = allParams[0];
            String[] parameters = Arrays.copyOfRange(allParams, 1, allParams.length);

            registerCommands.add(new RegisterCommand(commandName, parameters));
        });

        return registerCommands;
    }
}