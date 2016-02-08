package uk.gov.register.presentation;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;
import java.util.Optional;

public class RegisterDetail {
    private final String domain;
    private final int totalRecords;
    private final int totalEntries;
    private final int totalItems;
    private final Optional<Instant> lastUpdated;
    private final EntryView entryView;

    public RegisterDetail(
            String domain,
            int totalRecords,
            int totalEntries,
            int totalItems,
            Optional<Instant> lastUpdated,
            EntryView entryView) {
        this.domain = domain;
        this.totalRecords = totalRecords;
        this.totalEntries = totalEntries;
        this.totalItems = totalItems;
        this.lastUpdated = lastUpdated;
        this.entryView = entryView;
    }

    @JsonProperty("domain")
    public String getDomain() {
        return domain;
    }

    @SuppressWarnings("unused, used from template")
    @JsonProperty("last-updated")
    public String getLastUpdatedTime() {
        return lastUpdated.map(Instant::toString).orElse("");
    }

    @JsonProperty("record")
    public EntryView getEntry() {
        return entryView;
    }

    @SuppressWarnings("unused, used from template")
    @JsonProperty("total-entries")
    public int getTotalEntries() {
        return totalEntries;
    }

    @SuppressWarnings("unused, used from template")
    @JsonProperty("total-items")
    public int getTotalItems() {
        return totalItems;
    }

    @SuppressWarnings("unused, used from template")
    @JsonProperty("total-records")
    public int getTotalRecords() {
        return totalRecords;
    }
}
