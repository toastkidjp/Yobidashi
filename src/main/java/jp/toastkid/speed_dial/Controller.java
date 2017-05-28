/*
 * Copyright (c) 2017 toastkidjp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package jp.toastkid.speed_dial;

import java.util.Optional;

import org.apache.commons.lang3.StringUtils;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import jp.toastkid.yobidashi.message.ArticleSearchMessage;
import jp.toastkid.yobidashi.message.Message;
import jp.toastkid.yobidashi.message.SnackbarMessage;
import jp.toastkid.yobidashi.message.WebSearchMessage;
import jp.toastkid.yobidashi.models.Defines;

/**
 * Speed Dial's controller.
 *
 * @author Toast kid
 */
public class Controller {

    /** Speed dial's scene graph file. */
    public static final String FXML = Defines.SCENE_DIR + "/SpeedDial.fxml";

    /** Root pane. */
    @FXML
    private Pane root;

    /** title. */
    @FXML
    private Label title;

    /** Input field. */
    @FXML
    private TextField input;

    /** Search type. */
    @FXML
    private ComboBox<String> type;

    /** Message sender. */
    private final Subject<Message> messenger = PublishSubject.create();

    /**
     * Return root pane.
     * @return root pane.
     */
    public Pane getRoot() {
        return root;
    }

    /**
     * Set passed text to title.
     * @param title title text
     */
    public void setTitle(final String title) {
        if (StringUtils.isBlank(title)) {
            return;
        }
        this.title.setText(title);
    }

    /**
     * Set type's zero.
     */
    public void setZero() {
        this.type.getSelectionModel().select(0);
    }

    /**
     * Do search.
     */
    @FXML
    private void search() {
        final String query = input.getText();
        final String t = type.getSelectionModel().getSelectedItem();
        if (StringUtils.isBlank(query) || StringUtils.isBlank(t)) {
            messenger.onNext(SnackbarMessage.make("You have to input any query."));
            return;
        }
        if ("Article".equals(t)) {
            messenger.onNext(ArticleSearchMessage.make(query));
            return;
        }
        messenger.onNext(WebSearchMessage.make(query, t));
    }

    /**
     * Set background image to root pane's background.
     * @param background
     */
    public void setBackground(final String background) {
        Optional.ofNullable(background).ifPresent(this::applyBackgroundStyle);
    }

    /**
     * Apply background style.
     * @param imageUrl
     */
    private void applyBackgroundStyle(final String imageUrl) {
        root.setStyle("-fx-background-image: url('" + imageUrl + "'); "
                + "-fx-background-position: center center; "
                + "-fx-background-size: cover;"
                + "-fx-background-repeat: stretch;");
    }

    /**
     * Setter of messenger's subscriber.
     * @return messenger.
     */
    public Disposable subscribe(final Consumer<Message> c) {
        return messenger.subscribe(c);
    }

    /**
     * Focus on input text field.
     */
    public void requestFocus() {
        input.requestFocus();
    }

}
