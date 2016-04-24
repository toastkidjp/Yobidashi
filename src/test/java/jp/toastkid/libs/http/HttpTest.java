package jp.toastkid.libs.http;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;

/**
 * Http's test.
 * @author Toast kid
 *
 */
public class HttpTest {

    /**
     * test using factories.
     * @throws Exception
     */
    @Test
    public final void test() {
        final Response fetch = Http.GET.url("http://www.yahoo.co.jp").fetch();
        assertNotNull(fetch.text());
        assertNotNull(fetch.header());
    }

}
