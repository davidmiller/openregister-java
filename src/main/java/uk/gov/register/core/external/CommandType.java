package uk.gov.register.core.external;

public enum CommandType {
    ADD_ITEM("add-item"),
    APPEND_ENTRY("append-entry"),
    ASSERT_ROOT_HASH("assert-root-hash");

    private final String name;

    CommandType(String s) {
        name = s;
    }

    public boolean equalsName(String otherName) {
        return (otherName == null) ? false : name.equals(otherName);
    }

    public String toString() {
        return this.name;
    }
}
