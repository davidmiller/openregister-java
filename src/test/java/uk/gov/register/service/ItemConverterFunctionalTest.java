package uk.gov.register.service;

import com.fasterxml.jackson.databind.JsonNode;
import io.dropwizard.jackson.Jackson;
import org.junit.Test;
import uk.gov.register.RegisterConfiguration;
import uk.gov.register.configuration.FieldsConfiguration;
import uk.gov.register.configuration.RegisterDomainConfiguration;
import uk.gov.register.core.FieldValue;
import uk.gov.register.core.LinkValue;
import uk.gov.register.core.ListValue;
import uk.gov.register.core.StringValue;
import uk.gov.register.resources.RequestContext;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.instanceOf;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ItemConverterFunctionalTest {
    private FieldsConfiguration fieldsConfiguration;
    private RequestContext requestContext;
    private RegisterDomainConfiguration registerDomainConfiguration;

    //@Before
    public void setup() {
        fieldsConfiguration = mock(FieldsConfiguration.class);
        requestContext = mock(RequestContext.class);
        when(requestContext.getScheme()).thenReturn("http");
        registerDomainConfiguration = mock(RegisterDomainConfiguration.class);
    }

    @Test
    public void convert_shouldConvertFieldEntryToStringValue_whenFieldIsNeitherCurieOrHasRegister() {
        JsonNode jsonNode = mock(JsonNode.class);
        when(jsonNode.textValue()).thenReturn("Name for the citzens of a country.");
        Map.Entry<String, JsonNode> entry = mock(Map.Entry.class);
        when(entry.getKey()).thenReturn("citizen-names");
        when(entry.getValue()).thenReturn(jsonNode);

        fieldsConfiguration = new FieldsConfiguration(Optional.ofNullable(System.getProperty("fieldsYaml")));
        requestContext = new RequestContext();
        registerDomainConfiguration = new RegisterConfiguration();

        ItemConverter itemConverter = new ItemConverter(fieldsConfiguration, requestContext, registerDomainConfiguration);
        FieldValue result = itemConverter.convert(entry);

        assertThat(result, instanceOf(StringValue.class));
        assertThat(result.getValue(), equalTo("Name for the citzens of a country."));
    }

    @Test
    public void convert_shouldConvertEntryToListValue() throws IOException {
        String jsonString = "{\"parent-bodies\":[\"test-1\",\"test-2\"]}";
        JsonNode res = Jackson.newObjectMapper().readValue(jsonString, JsonNode.class);
        Map.Entry<String, JsonNode> entry = mock(Map.Entry.class);
        when(entry.getKey()).thenReturn("parent-bodies");
        when(entry.getValue()).thenReturn(res);

        fieldsConfiguration = new FieldsConfiguration(Optional.ofNullable(System.getProperty("fieldsYaml")));
        requestContext = new RequestContext();
        registerDomainConfiguration = new RegisterConfiguration();

        ItemConverter itemConverter = new ItemConverter(fieldsConfiguration, requestContext, registerDomainConfiguration);
        FieldValue result = itemConverter.convert(entry);

        assertThat(result, instanceOf(ListValue.class));
    }

    @Test
    public void convert_shouldConvertEntryToLinkValue() throws IOException {
        JsonNode jsonNode = mock(JsonNode.class);
        when(jsonNode.textValue()).thenReturn("A school in the UK.");
        Map.Entry<String, JsonNode> entry = mock(Map.Entry.class);
        when(entry.getKey()).thenReturn("school");
        when(entry.getValue()).thenReturn(jsonNode);

        fieldsConfiguration = new FieldsConfiguration(Optional.ofNullable(System.getProperty("fieldsYaml")));
        requestContext = new RequestContext() {
            @Override
            public String getScheme() { return "http"; }
        };
        registerDomainConfiguration = new RegisterConfiguration();

        ItemConverter itemConverter = new ItemConverter(fieldsConfiguration, requestContext, registerDomainConfiguration);
        FieldValue result = itemConverter.convert(entry);

        assertThat(result, instanceOf(LinkValue.class));
        assertThat(result.getValue(), equalTo("A school in the UK."));
    }

    @Test
    public void convert_shouldConvertEntryToCurieValue() {
        JsonNode jsonNode = mock(JsonNode.class);
        when(jsonNode.textValue()).thenReturn("business:13245");
        Map.Entry<String, JsonNode> entry = mock(Map.Entry.class);
        when(entry.getKey()).thenReturn("business");
        when(entry.getValue()).thenReturn(jsonNode);

        fieldsConfiguration = new FieldsConfiguration(Optional.ofNullable(System.getProperty("fieldsYaml")));
        requestContext = new RequestContext() {
            @Override
            public String getScheme() { return "http"; }
        };
        registerDomainConfiguration = new RegisterConfiguration();

        ItemConverter itemConverter = new ItemConverter(fieldsConfiguration, requestContext, registerDomainConfiguration);
        FieldValue result = itemConverter.convert(entry);

        assertThat(result, instanceOf(LinkValue.CurieValue.class));
        assertThat(result.getValue(), equalTo("business:13245"));
    }
}