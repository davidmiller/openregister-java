package uk.gov.register.functional;

import org.apache.commons.codec.digest.DigestUtils;
import org.json.JSONException;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import uk.gov.register.functional.app.RegisterRule;

import javax.ws.rs.core.Response;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

public class ItemResourceFunctionalTest {
    @ClassRule
    public static RegisterRule register = new RegisterRule("address");
    private static String item1 = "{\"address\":\"6789\",\"street\":\"presley\"}";
    private static String item2 = "{\"address\":\"145678\",\"street\":\"ellis\"}";

    @Before
    public void publishTestMessages() throws Throwable {
        register.wipe();
        register.mintLines(item1, item2);
    }

    @Test
    public void jsonRepresentationOfAnItem() throws JSONException {
        String sha256Hex = DigestUtils.sha256Hex(item1);

        Response response = register.getRequest(String.format("/item/sha-256:%s.json", sha256Hex));

        assertThat(response.getStatus(), equalTo(200));

        JSONAssert.assertEquals(item1, response.readEntity(String.class), false);
    }

    @Test
    public void return404ResponseWhenItemNotExist() throws JSONException {
        Response response = register.getRequest("/item/sha-256:notExistHexValue");

        assertThat(response.getStatus(), equalTo(404));
    }

    @Test
    public void return404ResponseWhenItemHexIsNotInProperFormat() throws JSONException {
        Response response = register.getRequest("/item/notExistHexValue");

        assertThat(response.getStatus(), equalTo(404));
    }
}
