/*
 * Copyright (c) 2017 toastkidjp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package jp.toastkid.dialog;

import java.net.URL;
import java.util.ResourceBundle;

import com.jfoenix.controls.JFXSpinner;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.VBox;

/**
 * Progress Dialog's contoller.
 *
 * @author Toast kid
 */
public class ProgressDialogController implements Initializable {

    /** Progres bar */
    @FXML
    protected ProgressBar pb;

    /** Spinner.*/
    @FXML
    protected JFXSpinner pin;

    /** Label. */
    @FXML
    protected Label label;

    /** Background. */
    @FXML
    protected VBox background;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        pb.setProgress(0.0d);
    }

}
