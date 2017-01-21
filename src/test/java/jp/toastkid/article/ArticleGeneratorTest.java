package jp.toastkid.article;

import static org.junit.Assert.assertTrue;

import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Before;
import org.junit.Test;

import jp.toastkid.yobidashi.models.ConfigTest;

/**
 * {@link ArticleGenerator}'s test case.
 *
 * @author Toast kid
 */
public final class ArticleGeneratorTest {

    /** test resource path. */
    private static final String PATH
        = "src/test/resources/article/C6FCB5AD323031332D30382D333128C5DA29.md";

    /** Test object. */
    private ArticleGenerator gen;

    /**
     * Initialize test object.
     * @throws URISyntaxException
     */
    @Before
    public void setUp() throws URISyntaxException {
        gen = new ArticleGenerator(ConfigTest.makeConfig());
    }

    /**
     * Test {@link ArticleGenerator#convertToHtml(Path)}.
     */
    @Test
    public void test_convertToHtml() {
        final String convertToHtml = gen.convertToHtml(Paths.get(PATH));
        assertTrue(convertToHtml.startsWith(
                "<h2> [[日記2016-02-26(金)]] [[日記2016-02-25(木)]]</h2>"));
    }

    /**
     * Test {@link ArticleGenerator#decorate(String, Path)}.
     */
    @Test
    public void test_decorate() {
        final String decorated = gen.decorate("test", Paths.get(PATH));
        assertTrue(decorated.startsWith("<!DOCTYPE html>"));
        assertTrue(decorated.endsWith("</html>"));
    }

}
