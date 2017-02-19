package jp.toastkid.article.models;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Before;
import org.junit.Test;

import jp.toastkid.article.models.Article.Extension;

/**
 * {@link Article}'s test cases.
 * @author Toast kid
 *
 */
public class ArticleTest {

    /** Test resource Path. */
    private static final Path PATH
        = Paths.get("src/test/resources/article/C6FCB5AD323031332D30382D333128C5DA29.md");

    /** Testing object. */
    private Article a;

    /**
     * Initialize before each test.
     */
    @Before
    public void setUp() {
        a = new Article(PATH);
    }

    /**
     * Check disallow null object.
     */
    @Test(expected=IllegalArgumentException.class)
    public void checkNotNullable() {
        new Article(null);
    }

    /**
     * Check constructor.
     */
    @Test
    public final void testInitialize() {
        assertEquals("日記2013-08-31(土)", a.title);
        assertEquals(".md", a.extention());
    }

    /**
     * Check {@link Article#extention()}.
     */
    @Test
    public final void testExtension() {
        assertEquals(".md", a.extention());
    }

    /**
     * Check {@link Article#toInternalUrl()}.
     */
    @Test
    public final void testToInternalUrl() {
        assertEquals("file:///internal//md/C6FCB5AD323031332D30382D333128C5DA29.md", a.toInternalUrl());
    }

    /**
     * Check {@link Article#replace(File)()}.
     */
    @Test
    public final void testReplace() {
        final Path dest = Paths.get("C6FCB5AD323031332D30382D333128C5DA.slide");
        a.replace(dest);
        assertEquals(dest, a.path);
        assertEquals("日記2013-08-31(土", a.title);
        assertEquals("file:///internal//slide/C6FCB5AD323031332D30382D333128C5DA.slide", a.toInternalUrl());
        assertEquals(".slide", a.extention());
    }

    /**
     * Check {@link Article#lastModified()}.
     * @throws IOException
     */
    @Test
    public final void testLastModified() throws IOException {
        assertEquals(Files.getLastModifiedTime(PATH).toMillis(), a.lastModified());
    }

    /**
     * Check {@link Article#lastModifiedText()}.
     */
    @Test
    public final void testLastModifiedText() {
        assertNotNull(a.lastModifiedText());
    }

    /**
     * Check compareTo.
     */
    @Test
    public void testCompareTo() {
        assertEquals(0, a.compareTo(a));
        final Article b = mock(Article.class);
        b.title = "日記";
        assertEquals(13, a.compareTo(b));
    }

    /**
     * Check enable deep copy.
     */
    @Test
    public void testClone() {
        final Article clone = a.clone();
        assertNotSame(a, clone);
        assertEquals(a, clone);
    }

    /**
     * Check {@link Article#isValid()}.
     */
    @Test
    public void test_isValid() {
        assertTrue(a.isValid());
    }

    /**
     * Check {@link Article#toString()}.
     */
    @Test
    public void test_toString() {
        assertNotNull(a.toString());
    }

    /**
     * Check {@link Article#equals()}.
     */
    @Test
    public void test_equals() {
        assertFalse(a.equals(""));
    }

    /**
     * Check {@link Article#hashCode()}.
     */
    @Test
    public void test_hashCode() {
        assertEquals(1660617097L, a.hashCode());
    }

    /**
     * Check {@link Article.Extension#values()}.
     */
    @Test
    public void test_text() {
        assertEquals(".md", Extension.MD.text());
    }

    /**
     * Check {@link Article.Extension#values()}.
     */
    @Test
    public void test_valueOf() {
        assertEquals(Extension.valueOf("MD"), Extension.MD);
    }

    /**
     * Check {@link Article.Extension#values()}.
     */
    @Test
    public void test_values() {
        assertTrue(Extension.values().length != 0);
    }

}
