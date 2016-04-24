package jp.toastkid.libs.http;

import org.junit.Test;

/**
 * {@link Request}'s test.
 * @author Toast kid
 *
 */
public class RequestTest {

    /**
     * check not nullable.
     * @throws Exception
     */
    @Test(expected=IllegalArgumentException.class)
    public final void testNotNullable() throws Exception {
        new Request.Factory(null).url(null);
    }

    @Test
    public void test() {
        final String text = Http.GET.url("http://www.yahoo.co.jp/").fetch().text();
        System.out.println(text.split("\n").length);
    }

}
