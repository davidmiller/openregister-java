package uk.gov.register.db;

import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;
import org.skife.jdbi.v2.sqlobject.customizers.SingleValueResult;
import uk.gov.register.core.Item;
import uk.gov.register.db.mappers.ItemMapper;

import java.util.Collection;
import java.util.Optional;

public interface ItemQueryDAO {

    String CURRENT_HEAD_FILTER = " exists(select * from entry where item.sha256hex = entry.sha256hex and entry_number <= (select value from current_entry_number)) ";

    @SqlQuery("select * from item where sha256hex=:sha256hex and "+CURRENT_HEAD_FILTER)
    @SingleValueResult(Item.class)
    @RegisterMapper(ItemMapper.class)
    Optional<Item> getItemBySHA256(@Bind("sha256hex") String sha256Hash);

    //Note: This is fine for small data registers like country
    @SqlQuery("select * from item where "+CURRENT_HEAD_FILTER)
    @RegisterMapper(ItemMapper.class)
    Collection<Item> getAllItemsNoPagination();
}
