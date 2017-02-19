/**
 *
 */
package jp.toastkid.article.converter;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Test;

import jp.toastkid.yobidashi.models.ConfigTest;

/**
 * {@link MarkdownConverter}'s test.
 * @author Toast kid
 *
 */
public final class MarkdownConverterTest {

    /** Encoding. */
    private static final String RESOURCE_ENCODE = "UTF-8";

    /**
     * {@link MarkdownConverter#convert(java.lang.String, java.lang.String)}'s test case.
     * @throws URISyntaxException
     */
    @Test
    public void test_convert() throws URISyntaxException {
        final MarkdownConverter converter = new MarkdownConverter(ConfigTest.makeConfig());
        final String content = converter.convert(testPath(), RESOURCE_ENCODE)
                                .replaceAll("class=\"redLink\"", "");
        assertNotNull(content);
        assertTrue(content.contains(
                "ふっとのーと<a id=\"fn-back-1\" href=\"#fn-1\" title=\" ここに飛ぶ\">[1]</a>"));
        assertTrue(content.contains("<a id=\"fn-1\" href=\"#fn-back-1\">[1]</a> ここに飛ぶ"));
    }

    /**
     * Return test file Path.
     * @return
     * @throws URISyntaxException
     */
    private Path testPath() throws URISyntaxException {
        return Paths.get(getClass().getClassLoader()
                .getResource("article/C6FCB5AD323031332D30382D333128C5DA29.md").toURI());
    }
}
