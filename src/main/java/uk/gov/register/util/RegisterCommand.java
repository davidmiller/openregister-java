package uk.gov.register.util;

public class RegisterCommand {
    public final String commandName;
    public final String[] arguments;

    public RegisterCommand(String commandName, String[] arguments) {
        this.commandName = commandName;
        this.arguments = arguments;
    }

    public String getCommandName() {
        return commandName;
    }

    public String[] getCommandArguments() {
        return arguments;
    }
}