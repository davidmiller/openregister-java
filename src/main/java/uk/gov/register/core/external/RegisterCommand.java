package uk.gov.register.core.external;

import java.util.Map;

public class RegisterCommand {
    private String commandType;
    private Map<String, String> data;

    public RegisterCommand(String commandType, Map<String, String> data){
        this.commandType = commandType;
        this.data = data;
    }

    public Map<String, String> getData() {
        return data;
    }

    public String getCommandType() {
        return commandType;
    }
}
