/*
 * Copyright (c) 2017 toastkidjp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package jp.toastkid.loto6;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.stream.Collectors;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import jp.toastkid.libs.utils.MathUtil;

/**
 * Loto 6 number generator.
 * @author Toast kid
 *
 */
public class Loto6Controller implements Initializable {

    /** 最大値 */
    private static final int MAX           = 43;

    /** いくつ生成するか */
    private static final int GENERATE_SIZE = 6;

    /** 生成した数値をセットする. */
    @FXML
    public Label result;

    /** 生成結果を変化させる文字列. */
    @FXML
    public TextField inputWord;

    @Override
    public final void initialize(
            final URL url,
            final ResourceBundle resourcebundle
            ) {
        // NOP.
    }

    /**
     * 乱数を生成して result に表示する.
     */
    @FXML
    public void generate() {
        final Set<Integer> randomIntegerSet = MathUtil.getDailyRandomIntSet(
                GENERATE_SIZE,
                MAX,
                inputWord.getText(),
                false
                );
        result.setText(
                randomIntegerSet.stream().map(i -> i.toString()).collect(Collectors.joining(" / ")));
    }
}
