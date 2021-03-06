package uk.gov.register.functional;

import com.google.common.collect.ImmutableMap;
import org.apache.commons.codec.digest.DigestUtils;
import org.json.JSONException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import uk.gov.register.functional.app.RegisterRule;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.Map;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

public class RecordListResourceFunctionalTest {
    @ClassRule
    public static RegisterRule register = new RegisterRule("address");

    @Before
    public void publishTestMessages() {
        register.wipe();
        register.mintLines("{\"street\":\"ellis\",\"address\":\"12345\"}", "{\"street\":\"presley\",\"address\":\"6789\"}", "{\"street\":\"ellis\",\"address\":\"145678\"}", "{\"street\":\"updatedEllisName\",\"address\":\"145678\"}", "{\"street\":\"ellis\",\"address\":\"6789\"}");
    }

    @SuppressWarnings("unchecked")
    @Test
    public void newRecords_shouldReturnAllCurrentVersionsOnly() throws Exception {
        Response response = register.getRequest("/records.json");

        Map<String, Map<String, String>> responseMap = response.readEntity(Map.class);

        assertThat(responseMap.size(), equalTo(3));

        assertThat(responseMap.get("6789"), equalTo(ImmutableMap.builder()
                .put("entry-number", "5")
                .put("item-hash", "sha-256:" + DigestUtils.sha256Hex("{\"address\":\"6789\",\"street\":\"ellis\"}"))
                .put("entry-timestamp", responseMap.get("6789").get("entry-timestamp"))
                .put("street", "ellis")
                .put("address", "6789")
                .put("key", "6789").build()));

        assertThat(responseMap.get("145678"), equalTo(ImmutableMap.builder()
                .put("entry-number", "4")
                .put("item-hash", "sha-256:" + DigestUtils.sha256Hex("{\"address\":\"145678\",\"street\":\"updatedEllisName\"}"))
                .put("entry-timestamp", responseMap.get("145678").get("entry-timestamp"))
                .put("street", "updatedEllisName")
                .put("address", "145678")
                .put("key", "145678").build()));

        assertThat(responseMap.get("12345"), equalTo(ImmutableMap.builder()
                .put("entry-number", "1")
                .put("item-hash", "sha-256:" + DigestUtils.sha256Hex("{\"address\":\"12345\",\"street\":\"ellis\"}"))
                .put("entry-timestamp", responseMap.get("12345").get("entry-timestamp"))
                .put("street", "ellis")
                .put("address", "12345")
                .put("key", "12345").build()));

    }

    @Test
    public void newRecords_setsAppropriateFilenameForDownload() {
        Response response = register.getRequest("/records.json");
        assertThat(response.getStatus(), equalTo(200));
        assertThat(response.getHeaderString(HttpHeaders.CONTENT_DISPOSITION), containsString("filename=\"address-records.json\""));
    }

    @Test
    public void newRecords_hasLinkHeaderForNextAndPreviousPage() {
        Response response = register.target().path("/records.json").queryParam("page-index",1).queryParam("page-size",1)
                .request().get();
        assertThat(response.getHeaderString("Link"), equalTo("<?page-index=2&page-size=1>; rel=\"next\""));

        response = register.target().path("/records.json").queryParam("page-index",2).queryParam("page-size",1)
                .request().get();
        assertThat(response.getHeaderString("Link"), equalTo("<?page-index=3&page-size=1>; rel=\"next\",<?page-index=1&page-size=1>; rel=\"previous\""));

        response = register.target().path("/records.json").queryParam("page-index",3).queryParam("page-size",1)
                .request().get();
        assertThat(response.getHeaderString("Link"), equalTo("<?page-index=2&page-size=1>; rel=\"previous\""));
    }

    @Test
    public void newRecordsPageHasXhtmlLangAttributes() {
        Response response = register.getRequest("/records");

        Document doc = Jsoup.parse(response.readEntity(String.class));
        Elements htmlElement = doc.select("html");
        assertThat(htmlElement.size(), equalTo(1));
        assertThat(htmlElement.first().attr("lang"), equalTo("en"));
        assertThat(htmlElement.first().attr("xml:lang"), equalTo("en"));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void fetchAllRecordsForAKeyValueCombination() throws JSONException {
        Response response = register.getRequest("/records/street/ellis.json");
        Map<String, Map<String, String>> responseMap = response.readEntity(Map.class);

        assertThat(responseMap.size(), equalTo(2));

        assertThat(responseMap.get("6789"), equalTo(ImmutableMap.builder()
                .put("entry-number", "5")
                .put("item-hash", "sha-256:" + DigestUtils.sha256Hex("{\"address\":\"6789\",\"street\":\"ellis\"}"))
                .put("entry-timestamp", responseMap.get("6789").get("entry-timestamp"))
                .put("street", "ellis")
                .put("address", "6789")
                .put("key", "6789").build()));

        assertThat(responseMap.get("12345"), equalTo(ImmutableMap.builder()
                .put("entry-number", "1")
                .put("item-hash", "sha-256:" + DigestUtils.sha256Hex("{\"address\":\"12345\",\"street\":\"ellis\"}"))
                .put("entry-timestamp", responseMap.get("12345").get("entry-timestamp"))
                .put("street", "ellis")
                .put("address", "12345")
                .put("key", "12345").build()));
    }

    //Note: tests below will be removed once the old resources are deleted
    @Test
    public void oldFacetedResourceRedirectsToNewResource(){
        WebTarget target = register.target();
        target.property("jersey.config.client.followRedirects",false);
        Response response = target.path("/street/ellis.json").request().get();
        assertThat(response.getStatus(), equalTo(301));
        String expectedRedirect = "/records/street/ellis";
        URI location = URI.create(response.getHeaderString("Location"));
        assertThat(location.getPath(), equalTo(expectedRedirect));
    }
}

