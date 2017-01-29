package jp.toastkid.jobs;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.Test;

/**
 * {@link FileWatcherJob}'s test cases.
 *
 * @author Toast kid
 *
 */
public class FileWatcherJobTest {

    /**
     * Check of {@link FileWatcherJob#run()}.
     * @throws IOException
     */
    @Test
    public void testRun() throws IOException {
        final FileWatcherJob fileWatcherJob = new FileWatcherJob();
        fileWatcherJob.add(Files.createTempFile("temp", ".txt"));
        fileWatcherJob.add(Files.createTempFile("temp", ".txt"));
        final Path removable = Files.createTempFile("temp", ".txt");
        fileWatcherJob.add(removable);
        fileWatcherJob.remove(removable);
        fileWatcherJob.run();
        fileWatcherJob.stop();
        fileWatcherJob.clear();
    }

}
