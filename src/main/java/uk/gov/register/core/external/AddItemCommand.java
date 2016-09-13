package uk.gov.register.core.external;

import java.util.Map;

public class AddItemCommand extends RegisterCommand {
    public AddItemCommand(Map<String, String> data) {
        super(data);
    }
}
