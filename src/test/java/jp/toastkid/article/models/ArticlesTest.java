package jp.toastkid.article.models;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;

import org.junit.Test;

import jp.toastkid.yobidashi.models.Defines;

/**
 * {@link Articles}' test case.
 *
 * @author Toast kid
 */
public class ArticlesTest {

    /**
     * Check of {@link Articles#readAllArticleNames(String)}.
     */
    @Test
    public void test_readAllArticleNames() {
        final List<Article> articleNames = Articles.readAllArticleNames("src/test/resources/chart");
        assertEquals(4, articleNames.size());
    }

    /**
     * Check of {@link Articles#isInternalLink(String)}.
     */
    @Test
    public void test_isInternalLink() {
        assertFalse(Articles.isInternalLink("aaa"));;
        assertTrue(Articles.isInternalLink("file:///internal//md/C6FCB5AD5F32303137.md"));
    }

    /**
     * {@link Articles#findByTitle(String)}' test case.
     */
    @Test
    public void test_findByTitle() {
        final Article article = Articles.findByTitle("日記_2017");
        assertEquals("日記_2017", article.title);
        assertEquals("file:///internal//md/C6FCB5AD5F32303137.md", article.toInternalUrl());
    }

    /**
     * {@link Articles#findByUrl(String)}' test case.
     */
    @Test
    public void test_findByUrl() {
        final Article article = Articles.findByUrl("file:///internal//md/C6FCB5AD5F32303137.md");
        assertEquals("日記_2017", article.title);
        assertEquals("file:///internal//md/C6FCB5AD5F32303137.md", article.toInternalUrl());
    }

    /**
     * {@link Articles#convertTitle(java.nio.file.Path)}' test case.
     * @throws URISyntaxException
     */
    @Test
    public void test_convertTitle() throws URISyntaxException {
        assertEquals("日記_2017年", Articles.convertTitle(
                Paths.get(new URI("file:///internal//md/C6FCB5AD5F32303137C7AF.md"))));
        assertNull(Articles.convertTitle( Paths.get("")));
    }

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
                put("installDir", "installed:");
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
     * test {@link Articles#isValidContentPath(File)}.
     */
    @Test
    public void test_isValidContentPath() {
        // valid extensions.
        assertTrue(Articles.isValidContentPath(Paths.get("test.md")));

        // invalid extensions.
        assertFalse(Articles.isValidContentPath(Paths.get("test.txt")));
        assertFalse(Articles.isValidContentPath(Paths.get("test.slide")));
        assertFalse(Articles.isValidContentPath(Paths.get("test.pptx")));
        assertFalse(Articles.isValidContentPath(null));
        assertFalse(Articles.isValidContentPath(Paths.get("test")));
    }

}
