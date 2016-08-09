package jp.toastkid.gui.jfx.wiki.functions;

import static org.junit.Assert.assertTrue;

import org.eclipse.collections.impl.factory.Maps;
import org.junit.Test;

import jp.toastkid.gui.jfx.wiki.ArticleGenerator;
import jp.toastkid.gui.jfx.wiki.models.ViewTemplate;

/**
 * {@link ArticleGenerator} のテストケース.
 * @author Toast kid
 *
 */
public final class FunctionsTest {
    /**
     * テンプレートのパラメータ置換が正しくできることを確認する.
     */
    @Test
    public final void testGetHTMLContent() {
        final String htmlContent = ArticleGenerator.bindArgs(
            ViewTemplate.MATERIAL.getPath(),
            Maps.mutable.with("title", "タイトルでござい", "content", "こんてんと")
        );
        assertTrue(
            htmlContent.contains("<title>タイトルでござい</title>")
            && htmlContent.contains("こんてんと")
        );
    }

    /**
     * check {@link ArticleGenerator#isValidMusicFile(String)}'s behavior.
     */
    @Test
    public final void testIsValidMusicFile() {
        assertTrue(ArticleGenerator.isValidMusicFile("echo.mp3"));
    }

}
