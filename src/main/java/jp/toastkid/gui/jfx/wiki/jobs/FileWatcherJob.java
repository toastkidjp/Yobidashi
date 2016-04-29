package jp.toastkid.gui.jfx.wiki.jobs;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
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
                .select((file, ms) -> {return ms < file.lastModified();})
                .forEachKeyValue(Procedures2.throwing((file, ms) -> {
                    final Path copy
                        = Files.copy(file.toPath(), new File(backup, file.getName()).toPath());
                    System.out.println(copy);
                    }));
            try {
                TimeUtil.sleep(BACKUP_INTERVAL);
            } catch (final InterruptedException e) {
                //e.printStackTrace();
            }
        }
    }

    public void add(final File file) {
        targets.put(file, -1L);
    }

    public void remove(final File file) {
        targets.remove(file);
    }

    public void clear() {
        targets.clear();

    }

}
