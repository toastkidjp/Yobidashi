/*
 * Copyright (c) 2017 toastkidjp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package jp.toastkid.bmi;

import java.net.URL;
import java.util.ResourceBundle;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

/**
 * Controller.
 * @author Toast kid
 *
 */
public class Controller implements Initializable {

    /** Logger. */
    private static final Logger LOGGER = LoggerFactory.getLogger(Controller.class);

    /** 改行記号. */
    private static final String LINE_SEPARATOR = System.lineSeparator();

    /** 身長(cm). */
    @FXML
    private TextField height;

    /** 体重(kg). */
    @FXML
    private TextField weight;

    /** 結果を表示する. */
    @FXML
    private TextArea result;

    @Override
    public final void initialize(
            final URL url,
            final ResourceBundle resourcebundle
            ) {
        // NOP.
    }

    /**
     * 計算.
     */
    @FXML
    private void calculate() {
        if (this.height == null || this.weight == null) {
            return;
        }

        final String heightStr = this.height.getText();
        final String weightStr = this.weight.getText();
        if (StringUtils.isAnyBlank(heightStr, weightStr)) {
            return;
        }

        try {
            final double height = Double.parseDouble(heightStr) / 100.0d;
            final double weight = Double.parseDouble(weightStr);
            result.setText(makeBmiResult(bmi(height, weight), standardWeight(height)));
        } catch (final RuntimeException e) {
            LOGGER.error("Caught error.", e);
        }
    }

    /**
     * 身長に対する標準体重を計算.
     * @param height 身長(㎝)
     * @return 標準体重(㎏)
     */
    private double standardWeight(final double height) {
        double standard = height * height * 22;
        standard = Math.round(standard * 10);
        standard = standard / 10;
        return standard;
    }

    /**
     * BMI 値を計算.
     * @param height 身長(㎝)
     * @param weight 体重(㎏)
     * @return BMI値
     */
    private double bmi(final double height, final double weight) {
        double bmi = weight / (height * height);
        bmi = Math.round(bmi * 10);
        bmi = bmi / 10;
        return bmi;
    }

    /**
     * 結果の文字列を作成.
     * @param bmi BMI 値
     * @param standard 標準体重
     * @return 文字列
     */
    private String makeBmiResult(final double bmi, final double standard) {
        final StringBuilder textMessage = new StringBuilder();
        textMessage.append("BMI値 = ").append(bmi).append(LINE_SEPARATOR)
            .append("標準体重 = ").append(standard).append(LINE_SEPARATOR);
        if (bmi < 18.5){
            textMessage.append("やせ過ぎです。");
        } else if (18.5 <= bmi && bmi < 25){
            textMessage.append("標準です。");
        } else if (25   <= bmi && bmi < 30){
            textMessage.append("肥満です。");
        } else if (30   <= bmi ){
            textMessage.append("高度肥満です。");
        }
        return textMessage.toString();
    }

}
