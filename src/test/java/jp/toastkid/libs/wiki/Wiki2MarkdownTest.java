package jp.toastkid.libs.wiki;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;

import jp.toastkid.libs.utils.CollectionUtil;

import org.junit.Test;

/**
 * {@link Wiki2Markdown}'s test.
 * @author Toast kid
 *
 */
public class Wiki2MarkdownTest {

    /** テストケース用ファイルの置き場所. */
    private static final String TEST_RESOURCES_DIR = "src/test/resources/wiki";

    /** テストケース */
    private static final File TEST_FILE    = new File(
            TEST_RESOURCES_DIR,
            "C6FCB5AD323031332D30382D333128C5DA29.txt"
    );

    /**
     *
     * @throws IOException
     */
    @Test
    public final void test() throws IOException {
        final List<String> converted = Wiki2Markdown.convert(
                Files.readAllLines(TEST_FILE.toPath(), StandardCharsets.UTF_8));
        System.out.println(CollectionUtil.implode(converted, System.lineSeparator()));
    }

}
