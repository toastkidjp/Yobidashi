/*
 * Copyright (c) 2017 toastkidjp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package jp.toastkid.yobidashi.popup;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jfoenix.controls.JFXPopup;
import com.jfoenix.controls.JFXPopup.PopupHPosition;
import com.jfoenix.controls.JFXPopup.PopupVPosition;

import io.reactivex.functions.Consumer;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import jp.toastkid.yobidashi.message.Message;
import jp.toastkid.yobidashi.models.Defines;

/**
 * Hamburger popup.
 *
 * @author Toast kid
 *
 */
public class HamburgerPopup {

    /** Logger. */
    private static final Logger LOGGER = LoggerFactory.getLogger(HamburgerPopup.class);

    /** FXML file. */
    private static final String FXML_PATH = Defines.SCENE_DIR + "/HamburgerPopup.fxml";

    /** Controller. */
    private HamburgerPopupController controller;

    /** Popup object. */
    private JFXPopup popup;

    /**
     * {@link HamburgerPopup} builder.
     * @author Toast kid
     *
     */
    public static class Builder {

        /** Container. */
        private Pane container;

        /** Source. */
        private Node source;

        /** Consumer. */
        private Consumer<Message> sub;

        /**
         * Set popup container.
         * @param p {@link Pane}
         * @return This builder object.
         */
        public Builder setContainer(final Pane p) {
            this.container = p;
            return this;
        }

        /**
         * Set poput source.
         * @param n {@link Node}
         * @return This builder object.
         */
        public Builder setSource(final Node n) {
            this.source = n;
            return this;
        }

        /**
         * Set message subscriber.
         * @param sub {@link Consumer}
         * @return This builder object.
         */
        public Builder setConsumer(final Consumer<Message> sub) {
            this.sub = sub;
            return this;
        }

        /**
         * Build new object.
         * @return {@link HamburgerPopup} object.
         */
        public HamburgerPopup build() {
            return new HamburgerPopup(this);
        }
    }

    /**
     * Initialize controller.
     */
    private HamburgerPopup(final Builder b) {
        final long start = System.currentTimeMillis();
        final FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource(FXML_PATH));
        try {
            loader.load();
            controller = (HamburgerPopupController) loader.getController();
            popup = controller.getPopup();
            popup.setPopupContainer(b.container);
            popup.setSource(b.source);
            controller.subscribe(b.sub);
        } catch (final IOException e) {
            LOGGER.error("Caught error.", e);
        }
        LOGGER.info("Ended loading scene graph. {}[ms]", System.currentTimeMillis() - start);
    }

    /**
     * Show popup.
     */
    public void show() {
        popup.show(PopupVPosition.TOP, PopupHPosition.RIGHT);
    }

}
