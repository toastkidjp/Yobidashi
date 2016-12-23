package jp.toastkid.article;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.HashMap;

import org.junit.Test;

import jp.toastkid.article.models.Article;
import jp.toastkid.article.models.Defines;

/**
 * {@link ArticleGenerator}'s test case.
 *
 * @author Toast kid
 */
public final class ArticleGeneratorTest {

    /**
     * テンプレートのパラメータ置換が正しくできることを確認する.
     */
    @Test
    public final void testGetHTMLContent() {

        final String htmlContent = ArticleGenerator.bindArgs(
            Defines.PATH_TO_TEMPLATE,
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

    /**
     * Check {@link ArticleGenerator#makeNewContent(Article)}.
     */
    @Test
    public void testMakeNewContent() {
        final byte[] newContent = ArticleGenerator.makeNewContent(
                new Article(new File("C6FCB5AD323031332D30382D333128C5DA29.md")));
        assertNotNull(newContent);
        System.out.println(new String(newContent));
        assertTrue(new String(newContent).startsWith("# 2013-08-31(土)"));
    }

    /**
     * Check {@link ArticleGenerator#titleToFileName(String)}.
     */
    @Test
    public void test_titleToFileName() {
        final String titleToFileName = ArticleGenerator.titleToFileName("トマト");
        assertEquals("A5C8A5DEA5C8", titleToFileName);
    }

    /**
     * Check {@link ArticleGenerator#decodeBytedStr(String, String)}.
     */
    @Test
    public void test_decodeBytedStr() {
        final String decodeBytedStr = ArticleGenerator.decodeBytedStr("A5C8A5DEA5C8", "EUC-JP");
        assertEquals("トマト", decodeBytedStr);
    }

}
