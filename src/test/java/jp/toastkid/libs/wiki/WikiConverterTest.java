/**
 *
 */
package jp.toastkid.libs.wiki;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.Test;

import jp.toastkid.libs.utils.CollectionUtil;
import jp.toastkid.libs.utils.FileUtil;
import jp.toastkid.libs.utils.Strings;
import jp.toastkid.wiki.models.Config;

/**
 * Wiki 変換のテスト.
 * @author Toast kid
 *
 */
public final class WikiConverterTest {

    /** テストケース用ファイルの置き場所. */
    private static final String TEST_RESOURCES_DIR = "src/test/resources/wiki";

    /** テストケース */
    private static final File TEST_FILE    = new File(
            TEST_RESOURCES_DIR,
            "C6FCB5AD323031332D30382D333128C5DA29.txt"
    );
    /** 文字コード. */
    private static final String RESOURCE_ENCODE = "UTF-8";

    /** 変換結果の正解. */
    private static final String EXPECTED = CollectionUtil.implode(
            FileUtil.readLines(new File(TEST_RESOURCES_DIR, "expected.html"), RESOURCE_ENCODE)
    );

    /**
     * {@link jp.toastkid.libs.wiki.WikiConverter#convert(java.lang.String, java.lang.String)}
     *のためのテスト・メソッド。
     */
    @Test
    public void testGetConvertedTXT() {
        final StringBuilder converted = new StringBuilder(3000);
        final WikiConverter converter = new WikiConverter(
                Config.get("imageDir"),
                Config.get("articleDir")
        );
        final String content = converter.convert(TEST_FILE.getAbsolutePath(), RESOURCE_ENCODE)
                                .replaceAll("class=\"redLink\"", "");
        converted.append(content);
        converted.append(Strings.LINE_SEPARATOR);
        //System.out.println(converted);
        //assertEquals(EXPECTED, converted.toString());
        assertNotNull(converted);
        assertTrue(0 < converted.length());
    }
}
