package jp.toastkid.article;

import static org.junit.Assert.assertTrue;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Test;

/**
 * {@link ArticleGenerator}'s test case.
 *
 * @author Toast kid
 */
public final class ArticleGeneratorTest {

    /** test resource path. */
    private static final String PATH
        = "src/test/resources/article/C6FCB5AD323031332D30382D333128C5DA29.md";

    /**
     * Test {@link ArticleGenerator#convertToHtml(Path)}.
     */
    @Test
    public void test_convertToHtml() {
        final ArticleGenerator gen = new ArticleGenerator();
        final String convertToHtml = gen.convertToHtml(Paths.get(PATH));
        assertTrue(convertToHtml.startsWith(
                "<h2> [[日記2016-02-26(金)]] [[日記2016-02-25(木)]]</h2>"));
    }

    /**
     * Test {@link ArticleGenerator#decorate(String, Path)}.
     */
    @Test
    public void test_decorate() {
        final ArticleGenerator gen = new ArticleGenerator();
        final String decorated = gen.decorate("test", Paths.get(PATH));
        assertTrue(decorated.startsWith("<!DOCTYPE html>"));
        assertTrue(decorated.endsWith("</html>"));
    }

}
