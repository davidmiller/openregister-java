package uk.gov.register.configuration;

import org.jvnet.hk2.annotations.Contract;
import uk.gov.register.core.Field;

@Contract
public interface FieldsConfiguration {
    Field getField(String fieldName);
}
