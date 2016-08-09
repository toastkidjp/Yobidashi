package jp.toastkid.wiki;

import java.io.File;
import java.io.IOException;
import java.time.ZonedDateTime;

import org.eclipse.collections.impl.block.factory.Procedures;
import org.eclipse.collections.impl.list.fixed.ArrayAdapter;
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
            final Zip backup
                = new Zip("backup" + ZonedDateTime.now().toInstant().getEpochSecond() + ".zip");
            ArrayAdapter.adapt(new File(articleDir).listFiles())
                .select(file -> offsetMs < file.lastModified())
                .each(Procedures.throwing((file) -> backup.entry(file)));
            backup.doZip();
        } catch (final IOException e) {
            LOGGER.error("Caught error.", e);
        }
    }

}
