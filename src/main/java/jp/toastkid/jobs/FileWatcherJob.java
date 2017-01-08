package jp.toastkid.jobs;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.concurrent.TimeUnit;

import org.eclipse.collections.api.map.MutableMap;
import org.eclipse.collections.impl.factory.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

/**
 * Watch file for auto backup.
 *
 * @author Toast kid
 */
public class FileWatcherJob implements Runnable {

    /** Logger. */
    private static final Logger LOGGER = LoggerFactory.getLogger(FileWatcherJob.class);

    /** Backup interval. */
    private static final long BACKUP_INTERVAL = TimeUnit.SECONDS.toMillis(30L);

    /** File watcher task list. */
    private final MutableMap<Path, Long> targets = Maps.mutable.empty();

    @Override
    public void run() {
        final Path backup = Paths.get("backup");
        if (!Files.exists(backup) || !Files.isDirectory(backup)) {
            LOGGER.info("make backup dir.");
            try {
                Files.createDirectory(backup);
            } catch (final IOException e) {
                LOGGER.error("Error", e);
            }
        }
        Flux.<Path>create(emitter -> {
            targets
                .select(FileWatcherJob::isTarget)
                .forEachKeyValue((file, ms) -> emitter.next(file));
            emitter.complete();
        })
        .delaySubscriptionMillis(BACKUP_INTERVAL)
        .repeat()
        .subscribeOn(Schedulers.newElastic("FileWatcher"))
        .subscribe(path -> {
            try {
                final Path copy = Files.copy(
                        path, backup.resolve(path.getFileName().toString()),
                        StandardCopyOption.REPLACE_EXISTING
                        );
                LOGGER.info("Backup to " + copy);
                add(path);
            } catch (final IOException e) {
                LOGGER.error("Error", e);
            }
        });
    }

    /**
     * Is passed path job's target?
     * @param path
     * @param ms
     * @return
     */
    private static boolean isTarget(final Path path, final long ms) {
        try {
            return ms < Files.getLastModifiedTime(path).toMillis();
        } catch (final IOException e) {
            LOGGER.error("Error", e);
        }
        return false;
    }

    /**
     * Add file to watch list.
     * @param path article file.
     */
    public void add(final Path path) {
        try {
            targets.put(path, Files.getLastModifiedTime(path).toMillis());
        } catch (final IOException e) {
            LOGGER.error("Error", e);
        }
    }

    /**
     * Remove Path to list.
     * @param path
     */
    public void remove(final Path path) {
        targets.remove(path);
    }

    /**
     * Clear watch list.
     */
    public void clear() {
        targets.clear();
    }

}
