package uk.gov.register.db;

import org.skife.jdbi.v2.sqlobject.BindBean;
import org.skife.jdbi.v2.sqlobject.SqlBatch;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import uk.gov.register.core.Item;

public interface ItemDAO {
    @SqlBatch("insert into item(sha256hex, content) values(:sha256hex, :contentAsJsonb) on conflict do nothing")
    void insertInBatch(@BindBean Iterable<Item> items);

    @SqlUpdate("delete from item where exists(select * from entry where item.sha256hex = entry.sha256hex and (entry_number > (select value from current_entry_number)))")
    void cleanRubbish();
}
