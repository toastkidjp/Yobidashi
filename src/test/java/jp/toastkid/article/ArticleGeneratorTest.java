package jp.toastkid.article;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;

import org.junit.Test;

import jp.toastkid.yobidashi.Defines;

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
