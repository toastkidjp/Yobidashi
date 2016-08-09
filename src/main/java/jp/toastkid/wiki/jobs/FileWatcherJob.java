package jp.toastkid.wiki.jobs;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.concurrent.TimeUnit;

import org.eclipse.collections.api.map.MutableMap;
import org.eclipse.collections.impl.factory.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jp.toastkid.libs.utils.TimeUtil;
import reactor.core.publisher.Flux;

/**
 * watch file for auto backup.
 * @author Toast kid
 *
 */
public class FileWatcherJob implements Runnable {

    /** Logger. */
    private static final Logger LOGGER = LoggerFactory.getLogger(FileWatcherJob.class);

    /** backup interval. */
    private static final long BACKUP_INTERVAL = TimeUnit.SECONDS.toMillis(30L);

    /** file watcher task list. */
    private final MutableMap<File, Long> targets = Maps.mutable.empty();

    @Override
    public void run() {
        final File backup = new File("backup");
        if (!backup.exists() || !backup.isDirectory()) {
            LOGGER.info("make backup dir.");
            backup.mkdir();
        }
        Flux.<File>create(emitter -> {
            while (true) {
                Runtime.getRuntime().addShutdownHook(new Thread(() -> emitter.complete()));
                targets
                    .select((file, ms) -> ms < file.lastModified())
                    .forEachKeyValue((file, ms) -> emitter.next(file));
                try {
                    TimeUtil.sleep(BACKUP_INTERVAL);
                } catch (final InterruptedException e) {
                    emitter.fail(e);
                }
            }
        })
        .subscribe(file -> {
            try {
                final Path copy = Files.copy(
                        file.toPath(), new File(backup, file.getName()).toPath(),
                        StandardCopyOption.REPLACE_EXISTING
                        );
                LOGGER.info("Backup to " + copy);
                targets.put(file, file.lastModified());
            } catch (final IOException e) {
                LOGGER.error("Error", e);;
            }
        });
    }

    /**
     * add file to watch list.
     * @param file article file.
     */
    public void add(final File file) {
        targets.put(file, file.lastModified());
    }

    /**
     * remove file to list.
     * @param file
     */
    public void remove(final File file) {
        targets.remove(file);
    }

    /**
     * clear watch list.
     */
    public void clear() {
        targets.clear();

    }

}
