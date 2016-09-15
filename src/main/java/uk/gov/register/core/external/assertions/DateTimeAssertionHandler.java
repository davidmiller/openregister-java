package uk.gov.register.core.external.assertions;

import uk.gov.register.core.external.AssertionHandler;
import uk.gov.register.core.external.ExecutionResult;

import java.util.Map;

public class DateTimeAssertionHandler implements AssertionHandler {

    @Override
    public ExecutionResult validate(String data) {
        return ExecutionResult.Executed();
    }

    @Override
    public String getHandlerType() {
        return "date-time";
    }
}

