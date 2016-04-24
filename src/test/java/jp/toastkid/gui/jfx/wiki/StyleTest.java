package jp.toastkid.gui.jfx.wiki;

import static org.junit.Assert.*;
import jp.toastkid.gui.jfx.common.Style;

import org.eclipse.collections.impl.factory.primitive.CharLists;
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
        CharLists.immutable.of(result.toCharArray())
            .select(c -> {return Character.isAlphabetic(c) || Character.isDigit(c);})
            .each(c -> {assertTrue(Character.isUpperCase(c));});
    }

}
