package uk.gov.register.resources;

import uk.gov.register.core.RegisterReadOnly;
import uk.gov.register.views.ConsistencyProof;
import uk.gov.register.views.EntryProof;
import uk.gov.register.views.RegisterProof;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.security.NoSuchAlgorithmException;

@Path("/proof")
public class VerifiableLogResource {
    private final RegisterReadOnly register;

    @Inject
    public VerifiableLogResource(RegisterReadOnly register) {
        this.register = register;
    }

    @GET
    @Path("/register/merkle:sha-256")
    @Produces(MediaType.APPLICATION_JSON)
    public RegisterProof registerProof() throws NoSuchAlgorithmException {
        return register.getRegisterProof();
    }

    @GET
    @Path("/entry/{entry-number}/{total-entries}/merkle:sha-256")
    @Produces(MediaType.APPLICATION_JSON)
    public EntryProof entryProof(@PathParam("entry-number") int entryNumber, @PathParam("total-entries") int totalEntries) throws NoSuchAlgorithmException {
        return register.getEntryProof(entryNumber, totalEntries);
    }

    @GET
    @Path("/consistency/{total-entries-1}/{total-entries-2}/merkle:sha-256")
    @Produces(MediaType.APPLICATION_JSON)
    public ConsistencyProof consistencyProof(@PathParam("total-entries-1") int totalEntries1, @PathParam("total-entries-2") int totalEntries2) throws NoSuchAlgorithmException {
        return register.getConsistencyProof(totalEntries1, totalEntries2);
    }
}
