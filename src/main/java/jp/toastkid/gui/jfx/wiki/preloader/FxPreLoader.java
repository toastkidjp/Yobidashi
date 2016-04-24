package jp.toastkid.gui.jfx.wiki.preloader;

import javafx.application.Preloader;
import javafx.scene.Scene;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

/**
 *
 * @author Toast kid
 *
 */
public class FxPreLoader extends Preloader {

    private ProgressBar bar;

    private Stage stage;

    /**
     *
     * @return
     */
    private Scene makeScene() {
        bar = new ProgressBar();
        final BorderPane p = new BorderPane();
        p.setCenter(bar);
        return new Scene(p, 300, 150);
    }

    @Override
    public void start(final Stage stage) throws Exception {
        this.stage = stage;
        stage.setScene(makeScene());
        stage.show();
    }

    @Override
    public void handleProgressNotification(final ProgressNotification pn) {
        bar.setProgress(pn.getProgress());
    }

    @Override
    public void handleStateChangeNotification(final StateChangeNotification evt) {
        if (evt.getType() == StateChangeNotification.Type.BEFORE_START) {
            stage.hide();
        }
    }
}