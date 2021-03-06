package uk.gov.register.core;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import uk.gov.register.util.ISODateFormatter;

import java.time.Instant;

@JsonIgnoreProperties(ignoreUnknown=true)
@JsonPropertyOrder({"entry-number", "entry-timestamp", "item-hash", "key"})
public class Entry {
    private final int entryNumber;
    private final String sha256hex;
    private final Instant timestamp;
    private String key;

    public Entry(int entryNumber, String sha256hex, Instant timestamp, String key) {
        this.entryNumber = entryNumber;
        this.sha256hex = sha256hex;
        this.timestamp = timestamp;
        this.key = key;
    }

    @JsonIgnore
    public Instant getTimestamp() {
        return timestamp;
    }

    @SuppressWarnings("unused, used from DAO")
    @JsonIgnore
    public String getSha256hex() {
        return sha256hex;
    }


    @JsonIgnore
    public long getTimestampAsLong() {
        return timestamp.getEpochSecond();
    }

    @JsonProperty("entry-number")
    @JsonSerialize(using = ToStringSerializer.class)
    public Integer getEntryNumber() {
        return entryNumber;
    }

    @JsonProperty("item-hash")
    public String getItemHash() {
        return "sha-256:" + sha256hex;
    }

    @JsonProperty("entry-timestamp")
    public String getTimestampAsISOFormat() {
        return ISODateFormatter.format(timestamp);
    }

    @JsonProperty("key")
    public String getKey() {
        return key;
    }

    public static CsvSchema csvSchema() {
        CsvMapper csvMapper = new CsvMapper();
        csvMapper.disable(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY);
        return csvMapper.schemaFor(Entry.class);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Entry entry = (Entry) o;

        if (entryNumber != entry.entryNumber) return false;
        return sha256hex == null ? entry.sha256hex == null : sha256hex.equals(entry.sha256hex);
    }

    @Override
    public int hashCode() {
        int result = sha256hex != null ? sha256hex.hashCode() : 0;
        result = 31 * entryNumber + result;
        return result;
    }
}
