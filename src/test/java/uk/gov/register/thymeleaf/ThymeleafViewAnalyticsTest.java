package uk.gov.register.thymeleaf;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import uk.gov.register.resources.RequestContext;

import java.util.Optional;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class ThymeleafViewAnalyticsTest {
    @Mock
    RequestContext mockRequestContext;

    private final Optional<String> validCode = Optional.of("UA-12345678-1");

    @Test
    public void shouldAnalyticsCode() {
        ThymeleafView sutView = new ThymeleafView(mockRequestContext, "", null, () -> "test.register.gov.uk", () -> validCode);
        assertThat(sutView.getRegisterTrackingId(), equalTo(Optional.of("UA-12345678-1")));
    }
}
