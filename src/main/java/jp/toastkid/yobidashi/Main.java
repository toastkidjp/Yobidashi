/*
 * Copyright (c) 2017 toastkidjp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package jp.toastkid.yobidashi;

import java.io.IOException;

import io.reactivex.disposables.CompositeDisposable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.reactivex.Single;
import io.reactivex.disposables.Disposable;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.geometry.BoundingBox;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import jp.toastkid.yobidashi.models.Defines;

/**
 * Main class of JavaFX Application.
 *
 * @author Toast kid
 * @version 0.0.1
 */
public final class Main extends Application {

    /** Logger. */
    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    /** Path to icon of this app. */
    private static final String PATH_IMG_ICON = "images/Icon.png";

    /** fxml file. */
    private static final String FXML_PATH = Defines.SCENE_DIR + "/YobidashiMain.fxml";

    /** Controller. */
    private Controller controller;

    /** Starting ms. */
    private long start;

    /** Stage and Controller initializer disposable. */
    private CompositeDisposable closer;

    @Override
    public void start(final Stage stage) {
        start = System.currentTimeMillis();
        closer = new CompositeDisposable();

        try {
            launch(stage);
        } catch (final Throwable e) {
            LOGGER.error("Caught error.", e);
        } finally {
            if (closer != null) {
                closer.dispose();
            }
        }
    }

    /**
     * initialize method.
     * @param stage
     */
    private void launch(final Stage stage) {

        Disposable disposable = readScene().subscribe(scene -> {
            controller.setStage(stage);
            stage.getIcons()
                .add(new Image(getClass().getClassLoader().getResourceAsStream(PATH_IMG_ICON)));
            stage.setScene(scene);

            final Rectangle2D d = Screen.getPrimary().getVisualBounds();
            maximizeStage(stage);
            controller.setSize(d.getWidth(), d.getHeight());
            // setup searcher.
            controller.setupExpandables();
            controller.setupSideMenu();
            controller.setStatus(" Complete - " + (System.currentTimeMillis() - start) + "[ms]");

            stage.setOnCloseRequest(event -> this.closeApplication(stage));
            stage.centerOnScreen();
            stage.initStyle(StageStyle.TRANSPARENT);
            stage.show();
            LOGGER.info("{} Ended set stage size. {}[ms]",
                    Thread.currentThread().getName(),
                    (System.currentTimeMillis() - start)
                    );
        });
        closer.add(disposable);
    }

    /**
     * コントローラに stage を渡し、シーンファイルを読み込む.
     * @return Scene オブジェクト
     */
    private Single<Scene> readScene() {
        final long start = System.currentTimeMillis();
        final FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource(FXML_PATH));
        try {
            loader.load();
            controller = (Controller) loader.getController();
        } catch (final IOException e) {
            LOGGER.error("Caught error.", e);
            Platform.exit();
        }
        LOGGER.info("{} Ended loading scene graph. {}[ms]",
                Thread.currentThread().getName(),
                System.currentTimeMillis() - start);
        return Single.just(new Scene(controller.root));
    }

    /**
     * Maximized the stage.
     */
    private void maximizeStage(final Stage stage) {
        final Screen screen = Screen.getScreens().get(0);
        final Rectangle2D bounds = screen.getVisualBounds();
        final BoundingBox maximizedBox = new BoundingBox(
                bounds.getMinX(), bounds.getMinY(), bounds.getWidth(), bounds.getHeight());
        stage.setX(maximizedBox.getMinX());
        stage.setY(maximizedBox.getMinY());
        stage.setWidth(maximizedBox.getWidth());
        stage.setHeight(maximizedBox.getHeight());
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        controller = null;
    }

    /**
     * Close this application.
     * @param stage
     */
    void closeApplication(final Stage stage) {
        stage.close();
        Platform.exit();
        System.exit(0);
    }

    /**
     * Launcher method.
     *
     * @param args
     */
    public static void main(final String[] args) {
        Application.launch(Main.class);
    }
}