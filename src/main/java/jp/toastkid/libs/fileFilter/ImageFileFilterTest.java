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
public class ImageFileFilterTest {

    /** test oject. */
    private ImageFileFilter filter;

    /**
     * init test oject.
     */
    @Before
    public void setUp() {
        filter = new ImageFileFilter(true);
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
        assertTrue(filter.accept(new File("temp"), "tomato.png"));
        assertTrue(filter.accept(new File("temp"), ".jpg"));
        assertFalse(filter.accept(new File("temp"), "tomato"));
    }

}
