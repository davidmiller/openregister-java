package uk.gov.register.core.external;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import uk.gov.register.core.Item;
import uk.gov.register.db.EntryStore;

import java.util.Arrays;

public class AddItemCommandHandler implements CommandHandler {
    private EntryStore entryStore;

    public AddItemCommandHandler(EntryStore entryStore) {
        this.entryStore = entryStore;
    }

    @Override
    public void handle(RegisterCommand command) {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode content = mapper.convertValue(command.getData(), JsonNode.class);
        Item newItem = new Item(content);
        entryStore.itemDAO.insertInBatch(Arrays.asList(newItem));
    }

    @Override
    public String getHandlerType() {
        return String.valueOf(CommandType.ADD_ITEM);
    }

}
