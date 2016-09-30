package uk.gov.register.core;

import org.skife.jdbi.v2.Handle;
import uk.gov.register.db.ItemDAO;
import uk.gov.register.db.ItemQueryDAO;

import javax.inject.Inject;
import java.util.Collection;
import java.util.Optional;

public class ItemStore {
    @Inject
    private Handle handle;

    public void putItems(Iterable<Item> items) {
        handle.attach(ItemDAO.class).insertInBatch(items);
    }

    public Optional<Item> getItemBySha256(String sha256hex) {
        return handle.attach(ItemQueryDAO.class).getItemBySHA256(sha256hex);
    }

    public Collection<Item> getAllItems() {
        return handle.attach(ItemQueryDAO.class).getAllItemsNoPagination();
    }
}
