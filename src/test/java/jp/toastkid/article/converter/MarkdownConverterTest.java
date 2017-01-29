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

import jp.toastkid.libs.utils.Strings;
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
    public void testGetConvertedTXT() throws URISyntaxException {
        final MarkdownConverter converter = new MarkdownConverter(ConfigTest.makeConfig());
        final String content = converter.convert(testPath().toAbsolutePath().toString(), RESOURCE_ENCODE)
                                .replaceAll("class=\"redLink\"", "");
        final StringBuilder converted = new StringBuilder(3000);
        converted.append(content);
        converted.append(Strings.LINE_SEPARATOR);
        System.out.println(converted.toString());
        assertNotNull(converted);
        assertTrue(0 < converted.length());
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
