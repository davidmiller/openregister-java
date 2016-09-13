package uk.gov.register.functional;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.ImmutableMap;
import io.dropwizard.testing.ConfigOverride;
import io.dropwizard.testing.ResourceHelpers;
import io.dropwizard.testing.junit.DropwizardAppRule;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.JerseyClient;
import org.glassfish.jersey.client.JerseyClientBuilder;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;
import org.junit.rules.TestRule;
import uk.gov.register.core.Item;
import uk.gov.register.db.DestinationDBUpdateDAO;
import uk.gov.register.db.EntryDAO;
import uk.gov.register.db.ItemDAO;
import uk.gov.register.functional.app.CleanDatabaseRule;
import uk.gov.register.functional.db.TestDAO;
import uk.gov.register.functional.db.TestDBItem;
import uk.gov.register.functional.db.TestRecord;
import uk.gov.register.core.Entry;
import uk.gov.register.RegisterApplication;
import uk.gov.register.RegisterConfiguration;
import uk.gov.register.util.CanonicalJsonMapper;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;
import static uk.gov.register.functional.db.TestDBSupport.*;

public class ExternalRepresentationFunctionalTest {
    public static final String MIRROR_URL = "http://localhost:9000/load-mirror";

    private final DropwizardAppRule<RegisterConfiguration> appRule = new DropwizardAppRule<>(RegisterApplication.class,
            ResourceHelpers.resourceFilePath("test-app-config.yaml"),
            ConfigOverride.config("database.url", postgresConnectionString),
            ConfigOverride.config("jerseyClient.timeout", "3000ms"),
            ConfigOverride.config("register", "register"));

    @Rule
    public TestRule ruleChain = RuleChain.
            outerRule(new CleanDatabaseRule()).
            around(appRule);


    private final CanonicalJsonMapper canonicalJsonMapper = new CanonicalJsonMapper();


    @Test
    public void checkMessageIsConsumedAndStoredInDatabase() throws Exception {

//        AddItemCommandHandler aicm = new AddItemCommandHandler(null, null);
//        AppendEntryCommandHandler aecm = new AppendEntryCommandHandler(null, null);
//
//        CommandExecutor cm = new CommandExecutor();
//        cm.register(aicm);
//        cm.register(aecm);
//
//        cm.execute(new AddItemCommand());
//        cm.execute(new AddItemCommand());
//        cm.execute(new AppendEntryCommand());
//        cm.execute(new AppendEntryCommand());

    }


    private Response send(String... payload) {
        return authenticatingClient().target(MIRROR_URL)
                .request(MediaType.APPLICATION_JSON_TYPE)
                .post(Entity.json(String.join("\n", payload)));

    }

    private JerseyClient authenticatingClient() {
        ClientConfig configuration = new ClientConfig();
        configuration.register(HttpAuthenticationFeature.basic("foo", "bar"));
        return JerseyClientBuilder.createClient(configuration);
    }

    private Client testClient() {
        return new io.dropwizard.client.JerseyClientBuilder(appRule.getEnvironment())
                .using(appRule.getConfiguration().getJerseyClientConfiguration())
                .build("test client");
    }
}


