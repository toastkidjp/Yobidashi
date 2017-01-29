package jp.toastkid.yobidashi.models;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Test;
import org.testfx.framework.junit.ApplicationTest;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

/**
 * {@link BookmarkManager}'s test cases.
 *
 * @author Toast kid
 *
 */
public class BookmarkManagerTest extends ApplicationTest {

    /** Test object. */
    private BookmarkManager bookmarkManager;

    /** Test stage. */
    private Stage stage;

    /** Test bookmark file path. */
    private Path path;

    /**
     * Check of {@link BookmarkManager#edit()}.
     */
    @Test
    public void testEdit() {
        Platform.runLater(() -> {
            bookmarkManager.edit(stage);
            final Button button = (Button) lookup("Store").query();
            button.fire();
        });
    }

    /**
     * Check of {@link BookmarkManager#readLines()}.
     * @throws IOException
     */
    @Test
    public void testReadLines() throws IOException {
        assertEquals(Files.readAllLines(path), bookmarkManager.readLines());
    }

    @Override
    public void start(Stage stage) throws Exception {
        this.stage = stage;
        this.stage.setScene(new Scene(new HBox()));
        path = Paths.get(getClass().getClassLoader().getResource("bookmark/bookmark.txt").toURI());
        bookmarkManager = new BookmarkManager(path);
    }

}
