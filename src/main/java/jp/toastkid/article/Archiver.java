package jp.toastkid.article;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.ZonedDateTime;

import org.eclipse.collections.impl.block.factory.Procedures;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jp.toastkid.libs.Zip;

/**
 * Use for simple backup.
 *
 * @author Toast kid
 *
 */
public class Archiver {

    /** Logger. */
    private static final Logger LOGGER = LoggerFactory.getLogger(Archiver.class);

    /**
     * do simpleBackup.
     * @param articleDir
     * @param offsetMs
     */
    public void simpleBackup(final String articleDir, final long offsetMs) {
        try {
            final Path pathToZip
                = Paths.get("backup" + ZonedDateTime.now().toInstant().getEpochSecond() + ".zip");
            final Zip backup = new Zip(pathToZip);
            Files.list(Paths.get(articleDir))
                .filter(path -> isBackup(path, offsetMs))
                .forEach(Procedures.throwing(path -> backup.entry(path.toAbsolutePath())));
            backup.doZip();
        } catch (final IOException e) {
            LOGGER.error("Caught error.", e);
        }
    }

    /**
     * Return is backup target.
     * @param path
     * @param offsetMs
     * @return
     */
    private static boolean isBackup(final Path path, final long offsetMs) {
        try {
            return offsetMs < Files.getLastModifiedTime(path).toMillis();
        } catch (final IOException e) {
            e.printStackTrace();
        }
        return false;
    }

}
