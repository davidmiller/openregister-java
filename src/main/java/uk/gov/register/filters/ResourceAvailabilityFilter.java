package uk.gov.register.filters;

import uk.gov.register.configuration.ResourceConfiguration;
import uk.gov.register.resources.DownloadNotAvailable;
import uk.gov.register.views.ViewFactory;

import javax.inject.Inject;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import java.io.IOException;

@Provider
@DownloadNotAvailable
public class ResourceAvailabilityFilter implements ContainerRequestFilter {

    private final ResourceConfiguration resourceConfiguration;
    private final ViewFactory viewFactory;

    @Inject
    public ResourceAvailabilityFilter(ResourceConfiguration resourceConfiguration, ViewFactory viewFactory) {
        this.resourceConfiguration = resourceConfiguration;
        this.viewFactory = viewFactory;
    }

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        if (!resourceConfiguration.getEnableDownloadResource()) {
            Response response = Response
                    .status(Response.Status.NOT_IMPLEMENTED)
                    .entity(viewFactory.thymeleafView("not-implemented.html"))
                    .build();

            requestContext.abortWith(response);
        }
    }
}
