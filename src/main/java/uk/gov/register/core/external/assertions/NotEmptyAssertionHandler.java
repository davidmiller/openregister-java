package uk.gov.register.core.external.assertions;

import uk.gov.register.core.external.AssertionHandler;
import uk.gov.register.core.external.ExecutionResult;

import java.util.Map;

public class NotEmptyAssertionHandler implements AssertionHandler {

    @Override
    public ExecutionResult validate(String data) {
        if(data.isEmpty()){
            return ExecutionResult.Failed("data is not supposed to be empty");
        } else {
            return ExecutionResult.Executed();
        }
    }

    @Override
    public String getHandlerType() {
        return "not-empty";
    }
}
