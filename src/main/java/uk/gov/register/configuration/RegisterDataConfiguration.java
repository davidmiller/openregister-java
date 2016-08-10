package uk.gov.register.configuration;

import org.jvnet.hk2.annotations.Contract;
import uk.gov.register.core.RegisterData;

@Contract
public interface RegisterDataConfiguration {
    RegisterData getRegisterData();
}
