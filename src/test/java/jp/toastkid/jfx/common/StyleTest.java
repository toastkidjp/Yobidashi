package jp.toastkid.jfx.common;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * {@link Style}'s test cases.
 * @author Toast kid
 *
 */
public class StyleTest {

    /**
     * check {@link Style#getPath(String)}.
     */
    @Test
    public void testGetPath() {
        assertEquals(Style.DEFAULT, Style.getPath(null));
        assertEquals(Style.DEFAULT, Style.getPath(""));
        assertEquals(" ", Style.getPath(" "));
        assertEquals("notExists", Style.getPath("notExists"));
    }

    /**
     * check {@link Style#findFileNamesFromDir()}.
     */
    @Test
    public final void testFind() {
        final String result = Style.findFileNamesFromDir().toString();
        System.out.println(result);
        assertTrue(!result.contains(".css"));
        for (final char c : result.toCharArray()) {
            if (!Character.isAlphabetic(c) && !Character.isDigit(c)) {
                continue;
            }
            assertTrue(Character.isUpperCase(c));
        }
    }

}
