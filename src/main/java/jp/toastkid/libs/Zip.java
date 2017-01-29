package jp.toastkid.libs;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Simple Zip archiving.
 * TODO write test.
 *
 * @author Toast kid
 */
public class Zip implements Serializable {

    /**
     * serialVersionUID.
     */
    private static final long serialVersionUID = 1823230904435690540L;

    /** zip output stream. */
    private final ZipOutputStream out;

    /**
     * should pass output path and archive name.
     * @param pathToZip
     * @throws IOException
     */
    public Zip(final Path pathToZip) throws IOException {
        this.out = new ZipOutputStream(Files.newOutputStream(pathToZip));
    }

    /**
     * entry single file.
     * @param path
     * @throws IOException
     */
    public void entry(final Path path) throws IOException {
        if (out == null || !Files.isReadable(path)) {
            return;
        }
        final BufferedInputStream in = new BufferedInputStream(new FileInputStream(path.toFile()));
        final ZipEntry entry = new ZipEntry(path.getFileName().toString());
        out.putNextEntry(entry);
        int size;
        final byte[] buf = new byte[500000];
        while ((size = in.read(buf, 0, buf.length)) != -1) {
            out.write(buf, 0, size);
        }
        out.closeEntry();
        in.close();
    }

    /**
     * do zip.
     * @throws IOException
     */
    public void doZip() throws IOException {
        out.flush();
        out.close();
    }
}
