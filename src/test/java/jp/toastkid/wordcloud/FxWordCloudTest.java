package jp.toastkid.wordcloud;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.After;
import org.junit.Test;
import org.mockito.internal.util.reflection.Whitebox;
import org.testfx.framework.junit.ApplicationTest;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * {@link FxWordCloud}'s test.
 *
 * @author Toast kid
 *
 */
public class FxWordCloudTest extends ApplicationTest {

    /** Sample file path. */
    private static final String SAMPLE_PATH = "wordcloud/sample.txt";

    /** Stage. */
    private Stage stage;

    /** Test canvas. */
    private Pane canvas;

    /**
     * check using default value when called default Builder#build().
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     * @throws NoSuchFieldException
     * @throws SecurityException
     */
    @Test
    public void checkUsingDefault() throws IllegalArgumentException, IllegalAccessException,
                              NoSuchFieldException, SecurityException {
        final FxWordCloud fxwc = new FxWordCloud.Builder().build();
        assertEquals(FxWordCloud.MIN_FONT_SIZE,   Whitebox.getInternalState(fxwc, "minFontSize"));
        assertEquals(FxWordCloud.MAX_FONT_SIZE,   Whitebox.getInternalState(fxwc, "maxFontSize"));
    }

    /**
     * check using default value when called default Builder#build().
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     * @throws NoSuchFieldException
     * @throws SecurityException
     */
    @Test
    public void checkUsingParameter() throws IllegalArgumentException, IllegalAccessException,
                              NoSuchFieldException, SecurityException {
        final FxWordCloud fxwc = new FxWordCloud.Builder()
                .setMinFontSize(10.0)
                .setMaxFontSize(100.0)
                .build();
        assertEquals(10.0,   Whitebox.getInternalState(fxwc, "minFontSize"));
        assertEquals(100.0,   Whitebox.getInternalState(fxwc, "maxFontSize"));
    }

    /**
     * check using default value when called default Builder#build().
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     * @throws NoSuchFieldException
     * @throws SecurityException
     */
    @Test
    public void checkReplaceParameter() throws IllegalArgumentException, IllegalAccessException,
                              NoSuchFieldException, SecurityException {
        final FxWordCloud fxwc = new FxWordCloud.Builder()
                .setMinFontSize(-10.0)
                .setMaxFontSize(-100.0)
                .build();
        assertEquals(FxWordCloud.MIN_FONT_SIZE,   Whitebox.getInternalState(fxwc, "minFontSize"));
        assertEquals(FxWordCloud.MAX_FONT_SIZE,   Whitebox.getInternalState(fxwc, "maxFontSize"));
    }

    /**
     * {@link FxWordCloud}'s smoke test.
     * @throws URISyntaxException
     */
    @Test
    public void test_display() throws URISyntaxException {
        final Path path = Paths.get(getClass().getClassLoader().getResource(SAMPLE_PATH).toURI());
        Platform.runLater(() ->  {
            new FxWordCloud.Builder().build().draw(canvas, path.toString());
            stage.show();
            assertTrue(0 < canvas.getChildren().size());
            canvas.getChildren().stream().map(Label.class::cast)
                .forEach(label -> {
                    assertNotNull(label.getText());
                    assertTrue(10.0d < label.getFont().getSize());
                });
        });

    }

    @Override
    public void start(final Stage stage) throws Exception {
        this.stage = new Stage(StageStyle.DECORATED);
        this.canvas = new MasonryPane();
        this.stage.setScene(new Scene(canvas));
        this.stage.setResizable(false);
    }

    /**
     * Close current window.
     */
    @After
    public void tearDown() {
        Platform.runLater(stage::close);
    }

}
