package uk.gov.register.core.external;

import java.util.Map;

public class AppendEntryCommand extends RegisterCommand {
    public AppendEntryCommand(Map<String, String> data) {
        super(data);
    }
    //timestamp, hash, sha
}
