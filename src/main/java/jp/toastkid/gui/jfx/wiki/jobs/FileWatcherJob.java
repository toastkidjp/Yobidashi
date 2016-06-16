package jp.toastkid.gui.jfx.wiki.jobs;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.concurrent.TimeUnit;

import org.eclipse.collections.api.map.MutableMap;
import org.eclipse.collections.impl.block.factory.Procedures2;
import org.eclipse.collections.impl.factory.Maps;

import jp.toastkid.libs.utils.TimeUtil;

/**
 * watch file for auto backup.
 * @author Toast kid
 *
 */
public class FileWatcherJob implements Runnable {

    /** backup interval. */
    private static final long BACKUP_INTERVAL = TimeUnit.SECONDS.toMillis(30L);

    /** file watcher task list. */
    private final MutableMap<File, Long> targets = Maps.mutable.empty();

    @Override
    public void run() {
        while (true) {
            final File backup = new File("backup");
            if (!backup.exists() || !backup.isDirectory()) {
                System.out.println("make backup dir.");
                backup.mkdir();
            }
            targets
                .select((file, ms) -> ms < file.lastModified())
                .forEachKeyValue(Procedures2.throwing((file, ms) -> {
                    final Path copy = Files.copy(
                            file.toPath(), new File(backup, file.getName()).toPath(),
                            StandardCopyOption.REPLACE_EXISTING
                            );
                    System.out.println("Backup to " + copy);
                    targets.put(file, file.lastModified());
                    }));
            try {
                TimeUtil.sleep(BACKUP_INTERVAL);
            } catch (final InterruptedException e) {
                //e.printStackTrace();
            }
        }
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
