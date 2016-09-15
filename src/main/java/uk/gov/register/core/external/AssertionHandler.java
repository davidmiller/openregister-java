package uk.gov.register.core.external;

import java.util.Map;

public interface AssertionHandler {
    ExecutionResult validate(String data);
    String getHandlerType();
}

