package jp.toastkid.article;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.Test;

/**
 * {@link Archiver}'s test case.
 *
 * @author Toast kid
 *
 */
public class ArchiverTest {

    /**
     * Test of {@link Archiver#simpleBackup(Path, long)}.
     * @throws IOException
     */
    @Test
    public void testSimpleBackup() throws IOException {
        final Path targetDir = Files.createTempDirectory("bu");
        Files.createTempFile(targetDir, "content", ".txt");
        final Path simpleBackup = new Archiver().simpleBackup(targetDir, 0);
        final String name = simpleBackup.toString();
        assertTrue(name.startsWith("backup"));
        assertTrue(name.endsWith(".zip"));
        assertNotNull(simpleBackup);
        Files.delete(simpleBackup);
    }

}
