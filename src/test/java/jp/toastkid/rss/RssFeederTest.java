package jp.toastkid.rss;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Before;
import org.junit.Test;

/**
 * {@link RssFeeder}'s test.
 *
 * @author Toast kid
 *
 */
public class RssFeederTest {

    /** Test target. */
    private RssFeeder rssFeeder;

    /**
     * Set up object.
     */
    @Before
    public void setUp() {
        rssFeeder = new RssFeeder();
    }

    /**
     * Test {@link RssFeeder#run(Path)} with empty.
     * @throws URISyntaxException
     */
    @Test
    public void testRunWithEmpty() throws URISyntaxException {
        final String content = rssFeeder.run(getPath("rss/target/empty.txt"));
        assertEquals("", content);
    }

    /**
     * Test {@link RssFeeder#run(Path)}.
     * @throws URISyntaxException
     */
    @Test
    public void testRun_invalid() throws URISyntaxException {
        final String content = rssFeeder.run(getPath("rss/invalid"));
        assertEquals("", content);
    }

    /**
     * Test {@link RssFeeder#run(Path)}.
     * @throws URISyntaxException
     */
    @Test
    public void testRun() throws URISyntaxException {
        final String content = rssFeeder.run(getPath("rss/target"));
        assertTrue(content.contains("http://codezine.jp/"));
    }

    /**
     * Return Path object.
     * @param pathTo
     * @return
     * @throws URISyntaxException
     */
    private Path getPath(final String pathTo) throws URISyntaxException {
        return Paths.get(getClass().getClassLoader().getResource(pathTo).toURI());
    }

}
