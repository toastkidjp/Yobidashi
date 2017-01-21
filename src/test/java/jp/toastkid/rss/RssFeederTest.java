package jp.toastkid.rss;

import org.junit.Test;

/**
 * {@link RssFeeder}'s test.
 *
 * @author Toast kid
 *
 */
public class RssFeederTest {

    @Test
    public void testRun() {
        final String content = new RssFeeder().run();
        System.out.println(content);
    }

}
