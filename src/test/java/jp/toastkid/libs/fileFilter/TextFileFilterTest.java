package jp.toastkid.libs.fileFilter;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.Before;
import org.junit.Test;

/**
 * {@link TextFileFilter}'s test.
 * @author Toast kid
 *
 */
public class TextFileFilterTest {

    /** test oject. */
    private TextFileFilter filter;

    /**
     * init test oject.
     */
    @Before
    public void setUp() {
        filter = new TextFileFilter(true);
    }

    /**
     * check nullable.
     */
    @Test
    public void testNullable() {
        assertFalse(filter.accept(null, null));
    }

    /**
     * {@link TextFileFilter#accept(File, String)}.
     */
    @Test
    public void testAccept() {
        assertTrue(filter.accept(new File("temp"), "tomato.txt"));
        assertTrue(filter.accept(new File("temp"), ".txt"));
        assertFalse(filter.accept(new File("temp"), "tomato"));
    }

}
