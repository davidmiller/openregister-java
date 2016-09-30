package uk.gov.register.providers;

import org.glassfish.hk2.api.Factory;
import org.glassfish.jersey.process.internal.RequestScoped;
import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.Handle;

import javax.inject.Inject;
import javax.ws.rs.ext.Provider;

@Provider
public class HandleFactory implements Factory<Handle> {
    @Inject
    private DBI dbi;

    @Override
    @RequestScoped
    public Handle provide() {
        return dbi.open();
    }

    @Override
    public void dispose(Handle instance) {
        instance.close();
    }
}
