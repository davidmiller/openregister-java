package uk.gov.register.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import uk.gov.register.core.Entry;
import uk.gov.register.core.Item;

import java.io.IOException;
import java.time.Instant;

public class CommandHelper {
    private final ObjectMapper objectMapper;

    public CommandHelper() {
        this.objectMapper = new ObjectMapper();
    }

    public Item getItem(RegisterCommand command) {
        try {
            String itemStr = command.getCommandArguments()[0];
            JsonNode node = objectMapper.readTree(itemStr);

            return new Item(node);
        } catch (IOException ex) {
            throw new RuntimeException("Invalid item JSON");
        }
    }

    public Entry getEntry(RegisterCommand command, int entryNumber) {
        String[] arguments = command.getCommandArguments();
        Instant timestamp = Instant.parse(arguments[0]);
        String hash = arguments[1].split(":")[1];

        return new Entry(entryNumber, hash, timestamp);
    }

    public String getRootHash(RegisterCommand command) {
        return command.getCommandArguments()[0];
    }
}
