package jp.toastkid.article.models;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.HashMap;

import org.junit.Test;

import jp.toastkid.yobidashi.Defines;

/**
 * {@link Articles}' test case.
 *
 * @author Toast kid
 */
public class ArticlesTest {

    /**
     * Check {@link Articles#titleToFileName(String)}.
     */
    @Test
    public void test_titleToFileName() {
        final String titleToFileName = Articles.titleToFileName("トマト");
        assertEquals("A5C8A5DEA5C8", titleToFileName);
    }

    /**
     * Check {@link Articles#decodeBytedStr(String, String)}.
     */
    @Test
    public void test_decodeBytedStr() {
        final String decodeBytedStr = Articles.decodeBytedStr("A5C8A5DEA5C8", "EUC-JP");
        assertEquals("トマト", decodeBytedStr);
    }

    /**
     * テンプレートのパラメータ置換が正しくできることを確認する.
     */
    @Test
    public final void testGetHTMLContent() {

        final String htmlContent = Articles.bindArgs(
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
     * test {@link Articles#isValidContentFile(File)}.
     */
    @Test
    public void testIsValidContentFile() {
        // valid extensions.
        assertTrue(Articles.isValidContentFile(new File("test.md")));

        // invalid extensions.
        assertFalse(Articles.isValidContentFile(new File("test.txt")));
        assertFalse(Articles.isValidContentFile(new File("test.slide")));
        assertFalse(Articles.isValidContentFile(new File("test.pptx")));
        assertFalse(Articles.isValidContentFile(null));
        assertFalse(Articles.isValidContentFile(new File("test")));
    }

}
