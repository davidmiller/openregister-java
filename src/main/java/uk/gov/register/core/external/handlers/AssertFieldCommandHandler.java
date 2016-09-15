package uk.gov.register.core.external.handlers;

import uk.gov.register.core.external.CommandHandler;
import uk.gov.register.core.external.ExecutionResult;
import uk.gov.register.core.external.RegisterCommand;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class AssertFieldCommandHandler implements CommandHandler {

    private Map<String, List<String>> activeAssertions;

    public AssertFieldCommandHandler(Map<String, List<String>> activeAssertions) {
        this.activeAssertions = activeAssertions;
    }

    @Override
    public ExecutionResult handle(RegisterCommand command) {
        String fieldName = command.getData().get("field");
        String assertionName = command.getData().get("assertion");

        List<String> assertions = activeAssertions.containsKey(fieldName) ? activeAssertions.get(fieldName) : new ArrayList<>();

        assertions.add(assertionName);
        activeAssertions.put(command.getData().get("field"), assertions);

        return ExecutionResult.Executed();
    }

    @Override
    public String getHandlerType() {
        return "assert-field";
    }

}
