package jp.toastkid.yobidashi.models;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Before;
import org.junit.Test;

import jp.toastkid.yobidashi.models.Config.Key;

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
     * Test of {@link Config#get(jp.toastkid.yobidashi.models.Config.Key)}.
     */
    @Test
    public void testGet_Key() {
        assertEquals("Y!obidashi", c.get(jp.toastkid.yobidashi.models.Config.Key.APP_TITLE, "sub"));
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
     * Test of {@link Config#getInt(Key, int)}.}
     */
    @Test
    public void testGetInt() {
        assertEquals(10, c.getInt(Key.FONT_SIZE, -1));
        assertEquals(-1, c.getInt(Key.TEST, -1));
    }

    /**
     * Test of {@link Config#getInt(Key, int)}'s irregular case.
     */
    @Test(expected=NumberFormatException.class)
    public void testGetInt_IrregularCase() {
        assertEquals(-1, c.getInt(Key.APP_TITLE, -1));
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
     * Test of {@link Config#reload()}.
     * @throws IOException
     */
    @Test
    public void testReload_() throws IOException {
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

    /**
     * Test of {@link Config#store(Key, String)}.
     * @throws IOException
     */
    @Test
    public void testStore_Key_String() throws IOException {
        final long pre = Files.getLastModifiedTime(path).toMillis();
        c.store(Key.APP_TITLE, "modified");
        final long stored = Files.getLastModifiedTime(path).toMillis();
        assertTrue(stored > pre);
        c.reload();
        assertEquals("modified", c.get(Key.APP_TITLE));
        c.store(Key.APP_TITLE, "Y!obidashi");
    }

    /**
     * Test of {@link Config.Key}.
     */
    @Test
    public void testKey() {
        assertEquals("author", Key.AUTHOR.text());
        assertSame(Key.AUTHOR, Key.valueOf("AUTHOR"));
        assertTrue(0 != Key.values().length);
    }

}
