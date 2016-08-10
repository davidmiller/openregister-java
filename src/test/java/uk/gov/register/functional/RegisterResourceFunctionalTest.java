package uk.gov.register.functional;

import com.google.common.collect.ImmutableList;
import org.junit.Test;
import uk.gov.register.functional.db.TestEntry;

import javax.ws.rs.core.Response;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.core.IsNot.not;


public class RegisterResourceFunctionalTest extends FunctionalTestBase {

    private final Map<?,?> expectedAddressRegisterMap = getAddressRegisterMap();

    @Test
    public void registerJsonShouldContainEntryViewRegisterRegister() throws Throwable {
        populateAddressRegisterEntries();

        Response registerResourceFromAddressRegisterResponse = getRequest("address", "/register.json");
        assertThat(registerResourceFromAddressRegisterResponse.getStatus(), equalTo(200));

        Map registerResourceMapFromAddressRegister = registerResourceFromAddressRegisterResponse.readEntity(Map.class);

        assertThat(registerResourceMapFromAddressRegister.get("total-entries"), equalTo(5));
        assertThat(registerResourceMapFromAddressRegister.get("total-records"), equalTo(3));
        verifyStringIsAnISODate(registerResourceMapFromAddressRegister.get("last-updated").toString());

        Map<?,?> registerRecordMapFromAddressRegister = (Map)registerResourceMapFromAddressRegister.get("register-record");
        verifyStringIsAnISODate(registerRecordMapFromAddressRegister.get("entry-timestamp").toString());

        assertAddressRegisterMapIsEqualTo(registerRecordMapFromAddressRegister);
    }

    @Test
    public void registerJsonShouldGenerateValidResponseForEmptyDB(){
        Response registerResourceFromAddressRegisterResponse = getRequest("address", "/register.json");
        assertThat(registerResourceFromAddressRegisterResponse.getStatus(), equalTo(200));

        Map<String,?> registerResourceMapFromAddressRegister = registerResourceFromAddressRegisterResponse.readEntity(Map.class);

        assertThat(registerResourceMapFromAddressRegister.get("total-entries"), equalTo(0));
        assertThat(registerResourceMapFromAddressRegister.get("total-records"), equalTo(0));

        assertThat(registerResourceMapFromAddressRegister, not(hasKey("last-updated")));
    }

    private void populateAddressRegisterEntries() {
        dbSupport.publishEntries(ImmutableList.of(
                TestEntry.anEntry(1, "{\"name\":\"ellis\",\"address\":\"12345\"}"),
                TestEntry.anEntry(2, "{\"name\":\"presley\",\"address\":\"6789\"}"),
                TestEntry.anEntry(3, "{\"name\":\"ellis\",\"address\":\"145678\"}"),
                TestEntry.anEntry(4, "{\"name\":\"updatedEllisName\",\"address\":\"145678\"}"),
                TestEntry.anEntry(5, "{\"name\":\"ellis\",\"address\":\"6789\"}")
        ));
    }

    private void assertAddressRegisterMapIsEqualTo(Map<?, ?> sutAddressRecordMapInRegisterRegister) {
        for (Map.Entry entry : expectedAddressRegisterMap.entrySet()) {
            assertThat(sutAddressRecordMapInRegisterRegister, hasEntry(entry.getKey(), entry.getValue()));
        }
    }

    private void verifyStringIsAnISODate(String lastUpdated) {
        DateTimeFormatter isoFormatter = DateTimeFormatter.ISO_INSTANT;
        TemporalAccessor parsedDate = isoFormatter.parse(lastUpdated);
        assertThat(isoFormatter.format(parsedDate), equalTo(lastUpdated));
    }

    private HashMap<String, Object> getAddressRegisterMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("entry-number", "1");
        result.put("item-hash", "sha-256:6a7bcb516b4a465f3340a31ab42a35a9193e512608d9577388da4d99251880a8");
        result.put("entry-timestamp", "2016-04-21T10:14:21Z");
        result.put("text", "Postal addresses in the UK");
        result.put("phase", "alpha");
        result.put("fields", Arrays.asList("address", "property", "street", "locality", "town", "area", "postcode", "country", "latitude", "longitude"));
        result.put("register", "address");
        result.put("registry", "office-for-national-statistics");
        result.put("copyright", "Contains Ordnance Survey data © Crown copyright & database right 2015\n Contains Royal Mail data © Royal Mail copyright & database right 2015\n");
        return result;
    }
}
