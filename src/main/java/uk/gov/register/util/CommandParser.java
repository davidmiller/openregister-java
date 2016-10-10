package uk.gov.register.util;

public interface CommandParser {
    Iterable<RegisterCommand> parseCommands(String commandStr);
}
