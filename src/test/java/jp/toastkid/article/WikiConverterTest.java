/**
 *
 */
package jp.toastkid.article;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Test;

import jp.toastkid.article.converter.MarkdownConverter;
import jp.toastkid.libs.utils.Strings;
import jp.toastkid.yobidashi.Config;

/**
 * Wiki 変換のテスト.
 * @author Toast kid
 *
 */
public final class WikiConverterTest {

    /** テストケース用ファイルの置き場所. */
    private static final String TEST_RESOURCES_DIR = "src/test/resources/wiki";

    /** テストケース */
    private static final Path TEST_FILE    = Paths.get(
            TEST_RESOURCES_DIR,
            "C6FCB5AD323031332D30382D333128C5DA29.txt"
    );
    /** 文字コード. */
    private static final String RESOURCE_ENCODE = "UTF-8";

    /**
     * {@link jp.toastkid.article.converter.MarkdownConverter#convert(java.lang.String, java.lang.String)}
     *のためのテスト・メソッド。
     */
    @Test
    public void testGetConvertedTXT() {
        final StringBuilder converted = new StringBuilder(3000);
        final MarkdownConverter converter = new MarkdownConverter(Config.get("imageDir"));
        final String content = converter.convert(TEST_FILE.toAbsolutePath().toString(), RESOURCE_ENCODE)
                                .replaceAll("class=\"redLink\"", "");
        converted.append(content);
        converted.append(Strings.LINE_SEPARATOR);
        assertNotNull(converted);
        assertTrue(0 < converted.length());
    }
}
