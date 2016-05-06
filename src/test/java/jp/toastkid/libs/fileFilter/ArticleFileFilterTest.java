package jp.toastkid.libs.fileFilter;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.Before;
import org.junit.Test;

/**
 * {@link ArticleFileFilter}'s test.
 * @author Toast kid
 *
 */
public class ArticleFileFilterTest {

    /** test oject. */
    private ArticleFileFilter filter;

    /**
     * init test oject.
     */
    @Before
    public void setUp() {
        filter = new ArticleFileFilter(true);
    }

    /**
     * check nullable.
     */
    @Test
    public void testNullable() {
        assertFalse(filter.accept(null, null));
    }

    /**
     * {@link ArticleFileFilter#accept(File, String)}.
     */
    @Test
    public void testAccept() {
        assertTrue(filter.accept(new File("temp"), "tomato.txt"));
        assertTrue(filter.accept(new File("temp"), ".txt"));
        assertTrue(filter.accept(new File("temp"), ".md"));
        assertFalse(filter.accept(new File("temp"), "tomato.scala"));
        assertFalse(filter.accept(new File("temp"), "tomato.wiki"));
        assertFalse(filter.accept(new File("temp"), "tomato"));
    }

}
