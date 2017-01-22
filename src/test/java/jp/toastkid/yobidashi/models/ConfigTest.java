package jp.toastkid.yobidashi.models;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Before;
import org.junit.Test;

/**
 * {@link Config}'s test case.
 *
 * @author Toast kid
 *
 */
public class ConfigTest {

    /** Test object. */
    private Config c;

    /** Test configuration file's path. */
    private static Path path;

    /**
     * Initialize {@link Config} object.
     * @throws URISyntaxException
     */
    @Before
    public void setUp() throws URISyntaxException {
        if (c != null) {
            return;
        }
        c = makeConfig();
    }

    /**
     * Initialize {@link Config}.
     * @return
     * @throws URISyntaxException
     */
    public static Config makeConfig() throws URISyntaxException {
        path = Paths.get(ConfigTest.class.getClassLoader().getResource("conf/conf.properties").toURI());
        return new Config(path);
    }

    /**
     * Test of {@link Config#get(String, String)}.
     * @throws URISyntaxException
     */
    @Test
    public void test_get_substitute() throws URISyntaxException {
        assertEquals("substitute", c.get("NotExists", "substitute"));
    }

    /**
     * Test of {@link Config#get(jp.toastkid.yobidashi.models.Config.Key)}.
     */
    @Test
    public void testGetString() {
        assertEquals("Y!obidashi", c.get(jp.toastkid.yobidashi.models.Config.Key.APP_TITLE));
        assertEquals("D:/Article/Article/", c.get(jp.toastkid.yobidashi.models.Config.Key.ARTICLE_DIR));
        assertEquals("Toast kid", c.get(jp.toastkid.yobidashi.models.Config.Key.AUTHOR));
        assertEquals("D:/Article/", c.get(jp.toastkid.yobidashi.models.Config.Key.IMAGE_DIR));
        assertEquals("INTER", c.get(jp.toastkid.yobidashi.models.Config.Key.STYLESHEET));
    }

    /**
     * Test of {@link Config#reload()}.
     */
    @Test
    public void testReload() {
        c.reload();
        assertEquals("Y!obidashi", c.get(jp.toastkid.yobidashi.models.Config.Key.APP_TITLE));
        assertEquals("D:/Article/Article/", c.get(jp.toastkid.yobidashi.models.Config.Key.ARTICLE_DIR));
        assertEquals("Toast kid", c.get(jp.toastkid.yobidashi.models.Config.Key.AUTHOR));
        assertEquals("D:/Article/", c.get(jp.toastkid.yobidashi.models.Config.Key.IMAGE_DIR));
        assertEquals("INTER", c.get(jp.toastkid.yobidashi.models.Config.Key.STYLESHEET));
    }

    /**
     * Test of {@link Config#store()}.
     * @throws IOException
     */
    @Test
    public void testStore() throws IOException {
        final long pre = Files.getLastModifiedTime(path).toMillis();
        c.store();
        final long stored = Files.getLastModifiedTime(path).toMillis();
        assertTrue(stored > pre);
    }

}
