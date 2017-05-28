/*
 * Copyright (c) 2017 toastkidjp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package jp.toastkid.jfx.common.control;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Label;
import javafx.scene.control.Skin;
import javafx.scene.input.KeyCombination;

/**
 * Action label for make materialized menu.
 *
 * @author Toast kid
 */
public class MenuLabel extends Label {

    /**
     * The action handler associated with this text field, or
     * <tt>null</tt> if no action handler is assigned.
     *
     * The action handler is normally called when the user types the ENTER key.
     */
    private final ObjectProperty<EventHandler<ActionEvent>> onAction
        = new SimpleObjectProperty<EventHandler<ActionEvent>>() {

        @Override
        protected void invalidated() {
            setEventHandler(ActionEvent.ACTION, get());
        }
    };

    public final ObjectProperty<EventHandler<ActionEvent>> onActionProperty() {
        return onAction;
    }

    public final EventHandler<ActionEvent> getOnAction() {
        return onActionProperty().get();
    }

    public final void setOnAction(EventHandler<ActionEvent> value) {
        onActionProperty().set(value);
    }

    /**
     * The accelerator property enables accessing the associated action in one keystroke.
     * It is a convenience offered to perform quickly a given action.
     */
    private ObjectProperty<KeyCombination> accelerator;

    public final void setAccelerator(KeyCombination value) {
        acceleratorProperty().set(value);
    }

    public final KeyCombination getAccelerator() {
        return accelerator == null ? null : accelerator.get();
    }

    public final ObjectProperty<KeyCombination> acceleratorProperty() {
        if (accelerator == null) {
            accelerator = new SimpleObjectProperty<KeyCombination>(this, "accelerator");
        }
        return accelerator;
    }

    @Override
    protected Skin<?> createDefaultSkin() {
        return new MenuLabelSkin(this);
    }

    public MenuLabel() {
        this("");
    }

    public MenuLabel(final String text) {
        super(text);
        setOnMouseClicked(event -> getOnAction().handle(new ActionEvent()));
    }
}
