package uk.gov.register.configuration;

import org.junit.Test;
import uk.gov.register.core.RegisterData;
import uk.gov.register.core.RegisterMetadata;

import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class RegistersConfigurationFunctionalTest {
    @Test
    public void configuration_shouldReturnRegisterData_whenRegisterExists() {
        RegistersConfiguration configuration = new RegistersConfiguration(Optional.ofNullable(System.getProperty("registersYaml")));
        RegisterData data = configuration.getRegisterData("register");
        RegisterMetadata metadata = data.getRegister();

        assertThat("register", equalTo(metadata.getRegisterName()));
    }
}