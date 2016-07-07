package uk.gov.store;

import org.skife.jdbi.v2.sqlobject.*;
import org.skife.jdbi.v2.sqlobject.stringtemplate.UseStringTemplate3StatementLocator;
import uk.gov.mint.Entry;

@UseStringTemplate3StatementLocator("/sql/entry.sql")
public interface EntryDAO {
    @SqlUpdate
    void ensureSchema();
    @SqlUpdate("create table if not exists current_entry_number(value integer not null)")
    void ensureCurrentEntryNumberTable();
    @SqlUpdate(
            "   insert into current_entry_number(value)\n" +
            "        select (\n" +
            "            select case\n" +
            "                when (select max(entry_number) from entry) is null then 0\n" +
            "                else (select max(entry_number) from entry)\n" +
            "            end as t\n" +
            "        )\n" +
            "        where not exists (\n" +
            "            select 1 from current_entry_number\n" +
            "        );\n")
    void ensureCurrentEntryNumberInit();

    @SqlBatch("insert into entry(entry_number, sha256hex) values(:entry_number, :sha256hex)")
    void insertInBatch(@BindBean Iterable<Entry> entries);

    @SqlQuery("select value from current_entry_number")
    int currentEntryNumber();

    @SqlUpdate("update current_entry_number set value=:entry_number")
    void setEntryNumber(@Bind("entry_number") int currentEntryNumber);
}
