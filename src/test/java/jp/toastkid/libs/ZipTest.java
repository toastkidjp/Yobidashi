package jp.toastkid.libs;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.zip.ZipFile;

import org.junit.Test;

/**
 * {@link Zip}'s test cases.
 *
 * @author Toast kid
 *
 */
public class ZipTest {

    /**
     * Test of {@link Zip}'s zipping and unzipping.
     * @throws IOException
     */
    @Test
    public void test() throws IOException {
        final Path tempZip = Files.createTempFile("temp", ".zip");
        final Zip zip = new Zip(tempZip);
        final Path tempFile = Files.createTempFile("temp", ".txt");
        zip.entry(tempFile);
        zip.doZip();
        final ZipFile zfile = new ZipFile(tempZip.toFile());
        assertEquals(tempFile.getFileName().toString(), zfile.entries().nextElement().getName());
        zfile.close();
    }

}
