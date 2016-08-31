package uk.gov.register.configuration;

import uk.gov.register.core.Cardinality;
import uk.gov.register.core.Field;

public class FieldBuilder {
    public static Field field(String fieldName) {
        return new Field(fieldName, "string", null, Cardinality.ONE, null);
    }

    public static Field field(String fieldName, Cardinality cardinality) {
        return new Field(fieldName, "string", null, cardinality, null);
    }

    public static Field field(String fieldName, String datatype) {
        return new Field(fieldName, datatype, null, Cardinality.ONE, null);
    }
}
