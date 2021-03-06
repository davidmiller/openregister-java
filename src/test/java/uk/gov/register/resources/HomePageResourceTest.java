package uk.gov.register.resources;

import org.junit.Test;
import uk.gov.register.core.RegisterReadOnly;
import uk.gov.register.views.HomePageView;
import uk.gov.register.views.ViewFactory;

import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.*;

public class HomePageResourceTest {

    @Test
    public void shouldReturnPageViewWithValidValues() throws NoSuchAlgorithmException {
        int totalRecords = 5;
        int totalEntries = 6;
        Optional<Instant> lastUpdated = Optional.of(Instant.ofEpochMilli(1459241964336L));
        HomePageView homePageView = mock(HomePageView.class);

        RegisterReadOnly registerMock = mock(RegisterReadOnly.class);
        ViewFactory viewFactoryMock = mock(ViewFactory.class);

        when(registerMock.getTotalRecords()).thenReturn(totalRecords);
        when(registerMock.getTotalEntries()).thenReturn(totalEntries);
        when(registerMock.getLastUpdatedTime()).thenReturn(lastUpdated);
        when(viewFactoryMock.homePageView(totalRecords, totalEntries, lastUpdated)).thenReturn(homePageView);

        HomePageResource homePageResource = new HomePageResource(registerMock, viewFactoryMock, () -> Optional.of("trackingId"));
        homePageResource.home();

        verify(registerMock, times(1)).getTotalRecords();
        verify(registerMock, times(1)).getTotalEntries();
        verify(viewFactoryMock, times(1)).homePageView(totalRecords, totalEntries, lastUpdated);
    }

    @Test
    public void shouldRenderAnalyticsCodeIfPresent() throws Exception {
        RegisterReadOnly registerMock = mock(RegisterReadOnly.class);
        ViewFactory viewFactoryMock = mock(ViewFactory.class);

        HomePageResource homePageResource = new HomePageResource(registerMock, viewFactoryMock, () -> Optional.of("codeForTest"));

        String s = homePageResource.analyticsTrackingId();

        assertThat(s, equalTo("var gaTrackingId = \"codeForTest\";\n"));
    }

    @Test
    public void shouldRenderEmptyJsFileIfCodeIsAbsent() throws Exception {
        RegisterReadOnly registerMock = mock(RegisterReadOnly.class);
        ViewFactory viewFactoryMock = mock(ViewFactory.class);

        HomePageResource homePageResource = new HomePageResource(registerMock, viewFactoryMock, () -> Optional.empty());

        String s = homePageResource.analyticsTrackingId();

        assertThat(s, equalTo(""));
    }
}

