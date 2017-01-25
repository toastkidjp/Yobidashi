package jp.toastkid.yobidashi.dialog;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Collectors;

import org.junit.Test;
import org.mockito.internal.util.reflection.Whitebox;
import org.testfx.framework.junit.ApplicationTest;

import javafx.application.Platform;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * {@link ConfigDialog}'s test case.
 *
 * @author Toast kid
 *
 */
public class ConfigDialogTest extends ApplicationTest {

    /** Path to config. */
    private static final String PATH = "conf/conf.properties";

    /** Test object. */
    private ConfigDialog dialog;

    /**
     * Test of {@link ConfigDialog#showAndWait()}.
     * @throws URISyntaxException
     * @throws IOException
     */
    @Test
    public void testShow() throws URISyntaxException, IOException {
        dialog.loadConfig(Paths.get(getClass().getClassLoader().getResource(PATH).toURI()));
        assertEquals("Toast kid",           extractedInput("author"));
        assertEquals("D:/Article/Article/", extractedInput("articleFolder"));
        assertEquals("D:/Article/",         extractedInput("pictureFolder"));
        Platform.runLater(() -> {
            try {
                final Path storeTarget = Files.createTempFile("tempConf", ".properties");
                dialog.store(storeTarget);
                final String content
                    = Files.readAllLines(storeTarget).stream().collect(Collectors.joining());
                assertNotNull(content);
            } catch (final Exception e) {
                fail(e.getMessage());
            }
        });
    }

    /**
     * Extract input text from TextField.
     * @param name TextField's name
     * @return text
     */
    private String extractedInput(final String name) {
        return ((TextField) Whitebox.getInternalState(dialog, name)).getText();
    }

    @Override
    public void start(final Stage stage) throws Exception {
        dialog = new ConfigDialog(stage);
    }

}
