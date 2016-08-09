package jp.toastkid.wiki.models;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import java.io.File;

import org.junit.Before;
import org.junit.Test;

import jp.toastkid.wiki.models.Article;

/**
 * {@link Article}'s test cases.
 * @author Toast kid
 *
 */
public class ArticleTest {

    /** test resource path. */
    private static final String PATH
        = "src/test/resources/wiki/C6FCB5AD323031332D30382D333128C5DA29.txt";

    /** test resource file. */
    private static final File FILE = new File(PATH);

    /** testing object. */
    private Article a;

    /**
     * initialize before each test.
     */
    @Before
    public void setUp() {
        a = new Article(FILE);
    }

    /**
     * check disallow null object.
     */
    @Test(expected=IllegalArgumentException.class)
    public void checkNotNullable() {
        new Article(null);
    }

    /**
     * check constructor.
     */
    @Test
    public final void testInitialize() {
        assertEquals("日記2013-08-31(土)", a.title);
        assertEquals(".txt", a.extention());
        assertEquals(0, a.byteLength);
    }

    /**
     * check {@link Article#extention()}.
     */
    @Test
    public final void testExtension() {
        assertEquals(".txt", a.extention());
    }

    /**
     * check {@link Article#toInternalUrl()}.
     */
    @Test
    public final void testToInternalUrl() {
        assertEquals("/wiki/C6FCB5AD323031332D30382D333128C5DA29.txt", a.toInternalUrl());
    }

    /**
     * check {@link Article#replace(File)()}.
     */
    @Test
    public final void testReplace() {
        final File dest = new File("C6FCB5AD323031332D30382D333128C5DA.slide");
        a.replace(dest);
        assertEquals(dest, a.file);
        assertEquals("日記2013-08-31(土", a.title);
        assertEquals("/wiki/C6FCB5AD323031332D30382D333128C5DA.slide", a.toInternalUrl());
        assertEquals(".slide", a.extention());
    }

    /**
     * check {@link Article#lastModified()}.
     */
    @Test
    public final void testLastModified() {
        assertEquals(FILE.lastModified(), a.lastModified());
    }

    /**
     * check {@link Article#lastModifiedText()}.
     */
    @Test
    public final void testLastModifiedText() {
        assertEquals("2016/4/24(日) 11:27:32", a.lastModifiedText());
    }

    /**
     * check compareTo.
     */
    @Test
    public void testCompareTo() {
        assertEquals(0, a.compareTo(a));
        final Article b = mock(Article.class);
        b.title = "日記";
        assertEquals(13, a.compareTo(b));
    }

    /**
     * check enable deep copy.
     */
    @Test
    public void testClone() {
        final Article clone = a.clone();
        assertNotSame(a, clone);
        assertEquals(a, clone);
    }

    /**
     * test {@link Article#isValidContentFile(File)}.
     */
    @Test
    public void testIsValidContentFile() {
        // valid extensions.
        assertTrue(Article.isValidContentFile(new File("test.txt")));
        assertTrue(Article.isValidContentFile(new File("test.md")));
        assertTrue(Article.isValidContentFile(new File("test.slide")));

        // invalid extensions.
        assertFalse(Article.isValidContentFile(new File("test.pptx")));
        assertFalse(Article.isValidContentFile(null));
        assertFalse(Article.isValidContentFile(new File("test")));
    }

}
