package uk.gov.register.db;

import org.skife.jdbi.v2.sqlobject.*;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;
import org.skife.jdbi.v2.sqlobject.customizers.SingleValueResult;
import uk.gov.register.core.Item;
import uk.gov.register.db.mappers.ItemMapper;

import java.util.Optional;

public interface ItemDAO {

    @SqlUpdate("CREATE TABLE IF NOT EXISTS item (sha256hex VARCHAR PRIMARY KEY, content JSONB);" +
            "CREATE INDEX IF NOT EXISTS item_content_gin ON item USING gin(content jsonb_path_ops);")
    void ensureSchema();

    @SqlBatch("insert into item(sha256hex, content) values(:sha256hex, :contentAsJsonb) on conflict do nothing")
    void insertInBatch(@BindBean Iterable<Item> items);

    @SqlQuery("select * from item where sha256hex=:sha256hex")
    @SingleValueResult(Item.class)
    @RegisterMapper(ItemMapper.class)
    Optional<Item> getItemBySHA256(@Bind("sha256hex") String sha256Hash);
}
