package jp.toastkid.libs.http;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import jp.toastkid.libs.http.Request.HttpMethod;

/**
 * {@link Request}'s test.
 * @author Toast kid
 *
 */
public class RequestTest {

    /** Test object. */
    private Request request;

    /**
     * Set up test object.
     */
    @Before
    public void setUp() {
        request = new Request.Factory(null).url("https://www.yahoo.co.jp");
    }

    /**
     * check not nullable.
     * @throws Exception
     */
    @Test(expected=IllegalArgumentException.class)
    public final void testNotNullable() throws Exception {
        new Request.Factory(null).url(null);
    }

    /**
     * Test fetch.
     */
    @Test
    public void test() {
        final String text = Http.GET.url("http://www.yahoo.co.jp/").fetch().text();
        System.out.println(text.split("\n").length);
    }

    /**
     * Test {@link Request#}.
     */
    @Test
    public void test_build() {
        request.connectTimeout(100);
    }

    /**
     * Check {@link Request#toString()}.
     */
    @Test
    public final void test_toString() {
        assertTrue(request.toString().contains(
                "conn=sun.net.www.protocol.https.DelegateHttpsURLConnection:https://www.yahoo.co.jp"));
    }

    /**
     * Check {@link Request#hashCode()}.
     */
    @Test
    public final void test_hashCode() {
        assertTrue(0 != request.hashCode());
    }

    /**
     * Check {@link Request#equals(Object)}.
     */
    @Test
    public final void test_equals() {
        assertTrue(request.equals(request));
    }

    /**
     * Check {@link Request.HttpMethod#valueOf(String)}.
     */
    @Test
    public final void test_valueOf() {
        assertEquals(HttpMethod.GET, HttpMethod.valueOf("GET"));
    }

    /**
     * Check {@link Request.HttpMethod#values()}.
     */
    @Test
    public final void test_values() {
        assertTrue(HttpMethod.values().length != 0);
    }

}
