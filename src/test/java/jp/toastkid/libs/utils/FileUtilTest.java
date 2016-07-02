package jp.toastkid.libs.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.io.File;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import org.junit.Test;
import org.mockito.Mockito;

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
        assertEquals(Optional.empty(), FileUtil.findExtension((File) null));
        assertEquals(Optional.empty(), FileUtil.findExtension(" "));
        assertEquals(Optional.empty(), FileUtil.findExtension(" "));
        assertEquals(Optional.empty(), FileUtil.findExtension("　"));
        assertEquals(Optional.empty(), FileUtil.findExtension("tomato"));
        assertEquals(".js", FileUtil.findExtension("tomato.js").get());
        assertEquals(".pyc", FileUtil.findExtension("tomato.orange.pyc").get());
        assertEquals(".pyc", FileUtil.findExtension("...pyc").get());
        assertEquals(".txt", FileUtil.findExtension(new File("dummy.txt")).get());
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
     * check {@link FileUtil#isLastModifiedNdays(java.io.File, long)}.
     */
    @Test
    public void testIsLastModifiedNdays() {
        final File mock = Mockito.mock(File.class);
        when(mock.lastModified())
            .thenReturn(System.currentTimeMillis() - TimeUnit.HOURS.toMillis(71L));
        assertTrue(FileUtil.isLastModifiedNdays(mock, 3));
        when(mock.lastModified())
            .thenReturn(System.currentTimeMillis() - TimeUnit.HOURS.toMillis(73L));
        assertFalse(FileUtil.isLastModifiedNdays(mock, 3));
    }

    /**
     * {@link FileUtil#countCharacters(String, String)}.
     */
    @Test
    public void testCountCharacters() {
        assertEquals(6, FileUtil.countCharacters("src/test/resources/utils/file/a.txt", "UTF-8"));
    }

    /**
     * {@link FileUtil#removeExtension(String)}.
     */
    @Test
    public void testRemoveExtension() {
        assertEquals("a", FileUtil.removeExtension("a.txt"));
    }
}
