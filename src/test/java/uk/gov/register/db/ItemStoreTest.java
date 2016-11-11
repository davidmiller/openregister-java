package uk.gov.register.db;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.Before;
import org.junit.Test;
import uk.gov.register.core.Item;
import uk.gov.register.core.ItemStore;
import uk.gov.register.exceptions.ItemValidationException;
import uk.gov.register.service.ItemValidator;
import uk.gov.register.store.BackingStoreDriver;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

public class ItemStoreTest {
    private BackingStoreDriver backingStoreDriver;
    private ItemValidator itemValidator;
    private String registerName;

    @Before
    public void setup() {
        backingStoreDriver = mock(BackingStoreDriver.class);
        itemValidator = mock(ItemValidator.class);
        registerName = "register";
    }

    @Test
    public void putItem_shouldInsertItemIntoBackingStore_whenItemIsValidated() {
        Item item = mock(Item.class);
        ItemStore itemStore = new ItemStore(backingStoreDriver, itemValidator, registerName);
        itemStore.putItem(item);

        verify(itemValidator, times(1)).validateItem(anyString(), any(JsonNode.class));
        verify(backingStoreDriver, times(1)).insertItem(item);
    }

    @Test
    public void putItem_shouldNotInsertItemIntoBackingStore_whenItemIsNotValidated() {
        JsonNode jsonNode = mock(JsonNode.class);
        doAnswer(invocation -> {
            throw new ItemValidationException("", jsonNode);
        }).when(itemValidator).validateItem(anyString(), any(JsonNode.class));

        Item item = mock(Item.class);
        try {
            ItemStore itemStore = new ItemStore(backingStoreDriver, itemValidator, registerName);
            itemStore.putItem(item);
        } catch (ItemValidationException e) {
            // Do nothing
        }
        finally {
            verify(itemValidator, times(1)).validateItem(anyString(), any(JsonNode.class));
            verify(backingStoreDriver, never()).insertItem(item);
        }
    }
}