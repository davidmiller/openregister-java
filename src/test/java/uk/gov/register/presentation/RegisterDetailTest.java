package uk.gov.register.presentation;

import org.junit.Test;

import java.time.Instant;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

public class RegisterDetailTest {
    @Test
    public void getLastUpdatedTime_returnsTheEmptyStringIfNoTimeStampExists() {
        RegisterDetail registerDetail = new RegisterDetail("", 0, 0, 0, Optional.<Instant>empty(), null);
        assertThat(registerDetail.getLastUpdatedTime(), equalTo(""));
    }

    @Test
    public void getLastUpdatedTime_returnsTheTimestampIfExists() {
        RegisterDetail registerDetail = new RegisterDetail("", 0, 0, 0, Optional.of(Instant.ofEpochMilli(1411111111)), null);
        assertThat(registerDetail.getLastUpdatedTime(), equalTo("1970-01-17T07:58:31.111Z"));
    }
}
