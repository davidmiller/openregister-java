package uk.gov.register.resources;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Throwables;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.register.configuration.RegisterNameConfiguration;
import uk.gov.register.core.external.*;
import uk.gov.register.db.EntryStore;
import uk.gov.register.service.ItemValidator;
import uk.gov.register.util.ObjectReconstructor;
import uk.gov.register.views.ViewFactory;

import javax.annotation.security.PermitAll;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import java.lang.reflect.Array;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Path("/")
public class DataUpload {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    protected final ViewFactory viewFactory;
    private String registerPrimaryKey;
    private ICommandExecutor commandExecutor;
    private ObjectReconstructor objectReconstructor;
    private ItemValidator itemValidator;
    private EntryStore entryStore;

    @Inject
    public DataUpload(ViewFactory viewFactory, RegisterNameConfiguration registerNameConfiguration, ObjectReconstructor objectReconstructor, ItemValidator itemValidator, EntryStore entryStore, ICommandExecutor commandExecutor) {
        this.viewFactory = viewFactory;
        this.objectReconstructor = objectReconstructor;
        this.itemValidator = itemValidator;
        this.entryStore = entryStore;
        this.registerPrimaryKey = registerNameConfiguration.getRegister();
        this.commandExecutor = commandExecutor;
    }

    @Context
    HttpServletRequest httpServletRequest;

    @POST
    @PermitAll
    @Path("/load")
    public void load(String payload) {
        try {
            Iterable<JsonNode> objects = objectReconstructor.reconstruct(payload.split("\n"));
            objects.forEach(singleObject -> itemValidator.validateItem(registerPrimaryKey, singleObject));
            entryStore.load(registerPrimaryKey, objects);
        } catch (Throwable t) {
            logger.error(Throwables.getStackTraceAsString(t));
            throw t;
        }
    }

    @POST
//    @GET
    @PermitAll
    @Path("/load-mirror")
    public void loadMirror(String payload) {
        try {
            Iterable<JsonNode> objects = objectReconstructor.reconstruct(payload.split("\n"));
            ObjectMapper mapper = new ObjectMapper();
            List<RegisterCommand> commands = new ArrayList<>();
            objects.forEach(node -> {
                if (node.fieldNames().hasNext()) {
                    String commandType = node.fieldNames().next();
                    JsonNode message = node.get(commandType);

                    JsonNode rawCommandData = message.isTextual() ? node : message;

                    commands.add(new RegisterCommand(commandType, mapper.convertValue(rawCommandData, Map.class)));
                }
            });

//            entryStore.load2(registerPrimaryKey, commands);
            commandExecutor.execute(commands);


        } catch (Throwable t) {
            logger.error(Throwables.getStackTraceAsString(t));
            throw t;
        }
    }


}

