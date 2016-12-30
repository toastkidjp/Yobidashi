package jp.toastkid.libs.wiki;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import jp.toastkid.wiki.lib.Subheading;

/**
 * Subheading's test case.
 * @author Toast kid
 *
 */
public class SubheadingTest {
    /**
     * check initialize.
     */
    @Test
    public final void initializeTest() {
        final Subheading h = new Subheading("test", "test", 1);
        assertEquals("test", h.title);
        assertEquals("test", h.id);
        assertEquals(1, h.depth);
        assertTrue(h instanceof Subheading);
    }

}