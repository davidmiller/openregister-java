package uk.gov.store;

import org.skife.jdbi.v2.sqlobject.*;
import org.skife.jdbi.v2.sqlobject.stringtemplate.UseStringTemplate3StatementLocator;
import uk.gov.mint.Entry;

@UseStringTemplate3StatementLocator("/sql/entry.sql")
public abstract class EntryDAO {
    public void ensureSchema() {
        ensureEntryTable();
        if (!entryIndexExists()) {
            createEntryIndex();
        }
        ensureCurrentEntryNumberTable();
        ensureCurrentEntryNumberInit();
    }

    @SqlQuery("SELECT to_regclass('public.entry_sha256hex_index');")
    protected abstract boolean entryIndexExists();

    @SqlUpdate("create index entry_sha256hex_index on entry (sha256hex);")
    protected abstract void createEntryIndex();

    @SqlUpdate("create table if not exists entry (" +
            "entry_number integer primary key," +
            "sha256hex varchar," +
            "timestamp timestamp default (now() at time zone 'utc'))")
    public abstract void ensureEntryTable();

    @SqlUpdate("create table if not exists current_entry_number(value integer not null)")
    public abstract void ensureCurrentEntryNumberTable();

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
    public abstract void ensureCurrentEntryNumberInit();

    @SqlBatch("insert into entry(entry_number, sha256hex) values(:entry_number, :sha256hex)")
    public abstract void insertInBatch(@BindBean Iterable<Entry> entries);

    @SqlQuery("select value from current_entry_number")
    public abstract int currentEntryNumber();

    @SqlUpdate("update current_entry_number set value=:entry_number")
    public abstract void setEntryNumber(@Bind("entry_number") int currentEntryNumber);
}
