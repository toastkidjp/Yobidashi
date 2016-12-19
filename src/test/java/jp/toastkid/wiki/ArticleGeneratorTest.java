package jp.toastkid.wiki;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.HashMap;

import org.junit.Test;

import jp.toastkid.wiki.models.Article;
import jp.toastkid.wiki.models.ViewTemplate;

/**
 * {@link ArticleGenerator} のテストケース.
 * @author Toast kid
 *
 */
public final class ArticleGeneratorTest {

    /**
     * テンプレートのパラメータ置換が正しくできることを確認する.
     */
    @Test
    public final void testGetHTMLContent() {

        final String htmlContent = ArticleGenerator.bindArgs(
            ViewTemplate.SECOND.getPath(),
            new HashMap<String, String>() {
                private static final long serialVersionUID = 1L;
            {
                put("title", "タイトルでござい");
                put("content", "こんてんと");
                put("jarPath", "jar:");
                put("bodyAdditional", "");
                put("subheadings", "");
            }}
        );
        assertTrue(
            htmlContent.contains("<title>タイトルでござい</title>")
            && htmlContent.contains("こんてんと")
        );
    }

    @Test
    public void test() {
        final byte[] newContent = ArticleGenerator.makeNewContent(new Article(new File("C6FCB5AD323031332D30382D333128C5DA29.txt")));
        assertNotNull(newContent);
        assertTrue(new String(newContent).startsWith("* 2013-08-31(土)"));
    }

}
