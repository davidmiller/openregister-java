package uk.gov.register.resources;

public interface Pagination {
    int getTotalEntries();

    boolean isSinglePage();

    int getFirstEntryNumberOnThisPage();

    int getLastEntryNumberOnThisPage();

    boolean hasNextPage();

    boolean hasPreviousPage();

    int getNextPageNumber();

    int getPreviousPageNumber();

    String getNextPageLink();

    String getPreviousPageLink();

    int getTotalPages();
}
