package uk.gov.register.core.external;

import java.util.Map;

public abstract class RegisterCommand {
    private Map<String, String> data;

    public RegisterCommand(Map<String, String> data){
        this.data = data;
    }

    public Map<String, String> getData() {
        return data;
    }
}
