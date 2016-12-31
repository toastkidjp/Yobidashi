package jp.toastkid.article;

import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.Test;

/**
 * {@link ArticleGenerator}'s test case.
 *
 * @author Toast kid
 */
public final class ArticleGeneratorTest {

    /** test resource path. */
    private static final String PATH = "src/test/resources/article/C6FCB5AD323031332D30382D333128C5DA29.md";

    /**
     * Test {@link ArticleGenerator#convertToHtml(File)}.
     */
    @Test
    public void test_convertToHtml() {
        final ArticleGenerator gen = new ArticleGenerator();
        final String convertToHtml = gen.convertToHtml(new File(PATH));
        assertTrue(convertToHtml.startsWith("<h2> [[日記2016-02-26(金)]] [[日記2016-02-25(木)]]</h2>"));
    }

    /**
     * Test {@link ArticleGenerator#decorate(String, File)}.
     */
    @Test
    public void test_decorate() {
        final ArticleGenerator gen = new ArticleGenerator();
        final String decorated = gen.decorate("test", new File(PATH));
        assertTrue(decorated.startsWith("<!DOCTYPE html>"));
        assertTrue(decorated.endsWith("</html>"));
    }

}
