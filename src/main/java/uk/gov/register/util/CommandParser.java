package uk.gov.register.util;

import java.util.List;

public interface CommandParser {
    List<RegisterCommand> parseCommands(String commandStr);
}
