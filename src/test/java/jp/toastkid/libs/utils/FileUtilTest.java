package jp.toastkid.libs.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.awt.Rectangle;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

import org.junit.Test;

/**
 * {@link FileUtil}'s test.
 * @author Toast kid
 *
 */
public class FileUtilTest {

    /**
     * Test uriToPath().
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
     * Test findExtension().
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
     * Test {@link FileUtil#readDirLines(String)}.
     */
    @Test
    public void testReadDirLines() {
        assertEquals(
                "[a, bb, ccc, tomato, orange, apple]",
                FileUtil.readDirLines("src/test/resources/utils/file").toString()
                );
    }

    /**
     * Test {@link FileUtil#readDirLines(String)}.
     */
    @Test
    public void testReadDirLines_file() {
        assertTrue(FileUtil.readDirLines("src/test/resources/utils/file/a.txt").isEmpty());;
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
        assertEquals("abc", FileUtil.removeExtension("abc"));
        assertNull(FileUtil.removeExtension(null));
    }

    /**
     * Test {@link FileUtil#readLines(String, String)}.
     */
    @Test
    public void testReadLines() {
        final List<String> readLines = FileUtil.readLines("not_exists", "UTF-8");
        assertNotNull(readLines);
        assertTrue(readLines.isEmpty());
    }

    /**
     * Test {@link FileUtil#readLines(String, String)}.
     */
    @Test
    public void testReadLines_null() {
        final List<String> readLines = FileUtil.readLines((Path) null, "UTF-8");
        assertNotNull(readLines);
        assertTrue(readLines.isEmpty());
    }

    /**
     * Test {@link FileUtil#makeFileReader(String, String)}.
     * @throws URISyntaxException
     */
    @Test
    public void test_makeFileReader() throws URISyntaxException {
        final String path = Paths.get(
                getClass().getClassLoader().getResource("utils/file/a.txt").toURI()).toString();
        final BufferedReader reader = FileUtil.makeFileReader(path, "UTF-8");
        assertNotNull(reader);
        assertTrue(reader instanceof BufferedReader);
    }

    /**
     * Test {@link FileUtil#makeFileReader(String, String)}'s failure case.
     * @throws URISyntaxException
     */
    @Test
    public void test_makeFileReader_failure_case() {
        assertNull(FileUtil.makeFileReader("utils/file/notFound.txt", "UTF-8"));
    }

    /**
     * Test {@link FileUtil#makeFileWriter(String, String)}.
     * @throws URISyntaxException
     */
    @Test
    public void test_makeFileWriter() throws URISyntaxException {
        final String path = Paths.get(
                getClass().getClassLoader().getResource("utils/file/a.txt").toURI()).toString();
        final PrintWriter writer = FileUtil.makeFileWriter(path, "UTF-8");
        assertNotNull(writer);
        assertTrue(writer instanceof PrintWriter);
    }

    /**
     * Test {@link FileUtil#makeFileWriter(String, String)}'s failure case.
     * @throws URISyntaxException
     */
    @Test
    public void test_makeFileWriter_failure_case() {
        assertNull(FileUtil.makeFileWriter("utils/file/notFound.txt", "UTF-8"));
    }

    /**
     * Smoke test of {@link FileUtil#capture(String, Rectangle)}.
     * @throws IOException
     */
    @Test
    public void test_capture() throws IOException {
        FileUtil.capture(Files.createTempFile("temp", ".png").toAbsolutePath().toString(), new Rectangle(1, 1));
    }

    /**
     * {@link FileUtil#isImageFile(String)}'s test case.
     * @throws IOException
     */
    @Test
    public void test_isImageFile() throws IOException {
        assertTrue(FileUtil.isImageFile("sample.jpg"));
        assertTrue(FileUtil.isImageFile("sample.jpeg"));
        assertTrue(FileUtil.isImageFile("sample.png"));

        assertFalse(FileUtil.isImageFile("sample.txt"));
        assertFalse(FileUtil.isImageFile("sample.md"));
        assertFalse(FileUtil.isImageFile("sample"));
        assertFalse(FileUtil.isImageFile(""));
        assertFalse(FileUtil.isImageFile(null));
    }

    /**
     * Failure case of {@link FileUtil#capture(String, Rectangle)}.
     * @throws IOException
     */
    @Test(expected=IllegalArgumentException.class)
    public void test_capture_failure_case() throws IOException {
        FileUtil.capture(Files.createTempFile("temp", ".png").toAbsolutePath().toString(), new Rectangle());
    }

}
