package uk.gov.register.db;

import org.skife.jdbi.v2.sqlobject.*;
import uk.gov.register.core.Entry;

public interface EntryDAO {
    @SqlBatch("insert into entry(entry_number, sha256hex, timestamp, key) values(:entryNumber, :sha256hex, :timestampAsLong, :key)")
    void insertInBatch(@BindBean Iterable<Entry> entries);

    @SqlQuery("select value from current_entry_number")
    int currentEntryNumber();

    @SqlUpdate("update current_entry_number set value=:entryNumber")
    void setEntryNumber(@Bind("entryNumber") int currentEntryNumber);
}
