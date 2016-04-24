package jp.toastkid.gui.jfx.wiki.functions;

import static org.junit.Assert.assertTrue;

import jp.toastkid.gui.jfx.wiki.Functions;
import jp.toastkid.gui.jfx.wiki.models.ViewTemplate;

import org.eclipse.collections.impl.factory.Maps;
import org.junit.Test;

/**
 * {@link Functions} のテストケース.
 * @author Toast kid
 *
 */
public final class FunctionsTest {
    /**
     * テンプレートのパラメータ置換が正しくできることを確認する.
     */
    @Test
    public final void testGetHTMLContent() {
        final String htmlContent = Functions.bindArgs(
            ViewTemplate.CLASSIC.getPath(),
            Maps.mutable.with("title", "タイトルでござい", "content", "こんてんと")
        );
        assertTrue(
            htmlContent.contains("<title>タイトルでござい</title>")
            && htmlContent.contains("こんてんと")
        );
    }

    /**
     * check {@link Functions#isValidMusicFile(String)}'s behavior.
     */
    @Test
    public final void testIsValidMusicFile() {
        assertTrue(Functions.isValidMusicFile("echo.mp3"));
    }

}
