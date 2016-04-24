package jp.toastkid.gui.jfx.wiki.script;

import static org.junit.Assert.*;
import jp.toastkid.gui.jfx.wiki.script.Language;

import org.junit.Test;

/**
 * Language's test.
 * @author Toast kid
 *
 */
public class LanguageTest {

    /**
     * すべて大文字の時だけオブジェクトを返す.
     */
    @Test
    public final void testValueOf() {
        assertEquals(Language.GROOVY, Language.valueOf("GROOVY"));
        assertEquals(Language.JAVASCRIPT, Language.valueOf("JAVASCRIPT"));
        assertEquals(Language.PYTHON, Language.valueOf("PYTHON"));
    }

    /**
     * 小文字は不可.
     */
    @Test(expected=IllegalArgumentException.class)
    public final void notDefined() {
        Language.valueOf("python");
    }

    /**
     * check extension().
     */
    public final void testExtension() {
        assertEquals(".groovy", Language.extension("GROOVY"));
        assertEquals(".py", Language.extension("PYTHON"));
        assertEquals(".js", Language.extension("JAVASCRIPT"));
        assertEquals(".groovy", Language.extension((String) null));
    }

    /**
     * check extension().
     */
    public final void testExtensionWithEnum() {
        assertEquals(".groovy", Language.extension(Language.GROOVY));
        assertEquals(".py", Language.extension(Language.PYTHON));
        assertEquals(".js", Language.extension(Language.JAVASCRIPT));
        assertEquals(".groovy", Language.extension((Language) null));
    }
}
