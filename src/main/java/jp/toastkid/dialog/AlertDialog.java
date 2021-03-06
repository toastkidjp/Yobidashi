/*
 * Copyright (c) 2017 toastkidjp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package jp.toastkid.dialog;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import jp.toastkid.yobidashi.models.Defines;

/**
 * 簡単な確認ダイアログ.
 * JavaFX 8u40以前でも使用可能.
 *
 * @author Toast kid
 * @see <a href="http://d.hatena.ne.jp/aoe-tk/20130526/1369577773">
 * JavaFX2.2でダイアログを作る方法</a>
 */
public final class AlertDialog extends Application {

    /** Logger. */
    private static final Logger LOGGER = LoggerFactory.getLogger(AlertDialog.class);

    /** FXML ファイルのパス. */
    private static final String FXML_PATH = Defines.SCENE_DIR + "/AlertDialog.fxml";

    /** コントローラオブジェクト. */
    private AlertDialogController controller;

    /** Stage. */
    private Stage stage;

    /** Scene. */
    private Scene scene = null;

    /**
     * {@link AlertDialog}'s builder.
     * @author Toast kid
     *
     */
    public static class Builder {

        /** Parent window. */
        private final Window parent;

        /** Title. */
        private String title;

        /** Message. */
        private String message;

        /** Negative button's text. */
        private String negaText;

        /** Negative action. */
        private Runnable negaAction;

        /** Positive button's text. */
        private String posiText;

        /** Positive button's action. */
        private Runnable posiAction;

        /** Neutral button's text. */
        private String neutralText;

        /** Neutral button's action. */
        private Runnable neutralAction;

        /** Dialog's controls. */
        private final List<Node> controls;

        public Builder() {
            this(null);
        }

        public Builder(final Window parent) {
            this.parent = parent;
            controls = new ArrayList<>();
        }

        public Builder setTitle(final String title) {
            this.title = title;
            return this;
        }

        public Builder setMessage(final String message) {
            this.message = message;
            return this;
        }

        public Builder setOnPositive(final String title, final Runnable act) {
            this.posiText   = title;
            this.posiAction = act;
            return this;
        }

        public Builder setOnNeutral(final String title, final Runnable act) {
            this.neutralText   = title;
            this.neutralAction = act;
            return this;
        }

        public Builder setOnNegative(final String title, final Runnable act) {
            this.negaText   = title;
            this.negaAction = act;
            return this;
        }

        public Builder addControl(final Node... nodes) {
            Stream.of(nodes).forEach(controls::add);
            return this;
        }

        public AlertDialog build() {
            return new AlertDialog(this);
        }
    }

    /**
     * Constructor.
     * @param parent
     */
    public AlertDialog(final Builder b) {
        loadDialog(b.parent);

        // set Builder's parameter on Controller.
        controller.setOnPositive(b.posiText, b.posiAction);
        controller.setOnNegative(b.negaText, b.negaAction);
        controller.setOnNeutral(b.neutralText, b.neutralAction);

        if (StringUtils.isNotBlank(b.message)) {
            controller.setMessage(b.message);
        }

        if (b.controls != null && !b.controls.isEmpty()) {
            controller.addAll(b.controls);
        }

        stage.setTitle(b.title);
        controller.setTitle(b.title);

        if (b.parent == null
                || b.parent.getScene() == null
                || b.parent.getScene().getStylesheets() == null) {
            return;
        }
        // StyleSheet をコピーする.
        this.scene.getStylesheets().addAll(b.parent.getScene().getStylesheets());
    }

    /**
     * FXML からロードする.
     * @return Parent オブジェクト
     */
    private final void loadDialog(final Window window) {
        final FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource(FXML_PATH));
        try {
            if (scene == null) {
                scene = new Scene(loader.load());
            }
        } catch (final IOException e) {
            LOGGER.error("Error", e);;
        }
        controller = loader.getController();
        stage = new Stage(StageStyle.UTILITY);
        stage.setScene(scene);
        if (window != null) {
            stage.initOwner(window);
        }
        stage.initModality(Modality.WINDOW_MODAL);
        stage.setResizable(false);
    }

    /**
     * Show and wait dialog.
     */
    public void showAndWait() {
        stage.showAndWait();
    }

    /**
     * Show dialog. It's for use test.
     */
    public void show() {
        stage.show();
    }

    @Override
    public void start(final Stage arg0) throws Exception {
        /*
        showMessageDialog("title", "message", "test");
        showInputDialog("title", "message", "test", null);
        System.out.println("input - " + getInput());
        showInputDialog("title", "message", null, "checksi");
        System.out.println("check - " + isChecked());
        showInputDialog("title", "message", null, null);
        System.out.println("check - " + isChecked());
        //*/
    }

    /**
     * main method.
     * @param args
     */
    public static void main(final String[] args) {
        Application.launch(AlertDialog.class);
    }

    /**
     * show simple message dialog.
     * @param parent
     * @param title
     * @param message
     */
    public static void showMessage(final Window parent, final String title, final String message) {
        showMessage(parent, title, message, null);
    }

    /**
     * show simple message dialog.
     * @param parent
     * @param title
     * @param message
     * @param detail
     */
    public static void showMessage(
            final Window parent, final String title, final String message, final String detail) {
        final Builder builder = new AlertDialog.Builder(parent)
                .setTitle(title)
                .setMessage(message);
        if (StringUtils.isNotEmpty(detail)) {
            final TextArea textArea = new TextArea(){{
                setEditable(false);
                setText(detail);
            }};
            builder.addControl(textArea);
        }
        builder.build().showAndWait();
    }

    /**
     * Close this dialog.
     */
    public void close() {
        stage.close();
    }
}