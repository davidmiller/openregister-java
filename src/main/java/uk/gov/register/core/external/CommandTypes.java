package uk.gov.register.core.external;

/**
 * Created by christopherd on 13/09/2016.
 */
public enum CommandTypes {
    AddItem("add-item"),
    AppendEntry("append-entry");

    private final String name;

    private CommandTypes(String s) {
        name = s;
    }

    public boolean equalsName(String otherName) {
        return (otherName == null) ? false : name.equals(otherName);
    }

    public String toString() {
        return this.name;
    }
}
