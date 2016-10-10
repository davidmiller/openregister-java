package uk.gov.register.util;

public interface CommandHandler {
    void execute(RegisterCommand command);
    String getCommandName();
}