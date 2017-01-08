package jp.toastkid.libs.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

import org.junit.Test;

/**
 * FileUtil's test.
 * @author Toast kid
 *
 */
public class FileUtilTest {

    /**
     * test uriToPath().
     */
    @Test
    public void testUriToPath() {
        assertEquals(
                "D:/workspace/WikiClient/", FileUtil.uriToPath("file:///D:/workspace/WikiClient/"));
        assertEquals("", FileUtil.uriToPath("file:///"));
        assertEquals("file://", FileUtil.uriToPath("file://"));
        assertEquals("", FileUtil.uriToPath(""));
        assertEquals("  ", FileUtil.uriToPath("  "));
        assertEquals(null, FileUtil.uriToPath(null));
    }

    /**
     * test findExtension().
     */
    @Test
    public void testFindExtension() {
        assertEquals(Optional.empty(), FileUtil.findExtension(""));
        assertEquals(Optional.empty(), FileUtil.findExtension((String) null));
        assertEquals(Optional.empty(), FileUtil.findExtension(" "));
        assertEquals(Optional.empty(), FileUtil.findExtension(" "));
        assertEquals(Optional.empty(), FileUtil.findExtension("　"));
        assertEquals(Optional.empty(), FileUtil.findExtension("tomato"));
        assertEquals(".js", FileUtil.findExtension("tomato.js").get());
        assertEquals(".pyc", FileUtil.findExtension("tomato.orange.pyc").get());
        assertEquals(".pyc", FileUtil.findExtension("...pyc").get());
    }

    /**
     * test {@link FileUtil#readDirLines(String)}.
     */
    @Test
    public void testReadDirLines() {
        assertEquals(
                "[a, bb, ccc, tomato, orange, apple]",
                FileUtil.readDirLines("src/test/resources/utils/file").toString()
                );
    }

    /**
     * {@link FileUtil#countCharacters(String, String)}.
     */
    @Test
    public void testCountCharacters() {
        assertEquals(6, FileUtil.countCharacters("src/test/resources/utils/file/a.txt", "UTF-8"));
    }

    /**
     * {@link FileUtil#countCharacters(Path, String)}.
     */
    @Test
    public void testCountCharacters_Path_String() {
        assertEquals(
                6,
                FileUtil.countCharacters(Paths.get("src/test/resources/utils/file/a.txt"), "UTF-8")
                );
    }

    /**
     * {@link FileUtil#removeExtension(String)}.
     */
    @Test
    public void testRemoveExtension() {
        assertEquals("a", FileUtil.removeExtension("a.txt"));
    }

    /**
     * test {@link FileUtil#readLines(String, String)}.
     */
    @Test
    public void testReadLines() {
        final List<String> readLines = FileUtil.readLines("not_exists", "UTF-8");
        assertNotNull(readLines);
        assertTrue(readLines.isEmpty());
    }
}
