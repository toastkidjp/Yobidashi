/**
 *
 */
package jp.toastkid.libs.utils;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Before;
import org.junit.Test;

/**
 * {@link AobunUtils}'s test case.
 *
 * @author Toast kid
 *
 */
public class AobunUtilsTest {

    /** Test file path string. */
    private static final String TEST_FILE_PATH = "libs/aobun/C6FCB5AD323031362D30332D303428B6E229.md";

    /** Temp directory. */
    private Path directory;

    /** Source file path. */
    private Path source;

    /**
     * Initialize temp directory and source path.
     * @throws IOException
     * @throws URISyntaxException
     */
    @Before
    public void setUp() throws IOException, URISyntaxException {
        directory = Files.createTempDirectory("aobun");
        source = Paths.get(getClass().getClassLoader().getResource(TEST_FILE_PATH).toURI());
    }

    /**
     * {@link jp.toastkid.libs.utils.AobunUtils#docToTxt(java.lang.String)} のためのテスト・メソッド。
     * @throws IOException
     * @throws URISyntaxException
     */
    @Test
    public void testDocToTxtString() throws IOException {
        AobunUtils.docToTxt(source, directory);

        final Path result = Files.list(directory).findFirst().get();

        final String content = new String(Files.readAllBytes(result), StandardCharsets.UTF_8);
        //System.out.println(content);
        assertTrue(content.contains("日記2016－03－04（金） 2016-03-04(金)［＃「2016-03-04(金)」は大見出し］"));
        assertTrue(content.contains(" 今日の午前問題［＃「 今日の午前問題」は中見出し］"));
        assertTrue(content.contains(" デュアルシステム［＃「 デュアルシステム」は小見出し］"));
        assertTrue(content.contains("両系統《りょうけいとう》"));
        assertTrue(content.contains("---------------------------------------------------"));
    }

}
