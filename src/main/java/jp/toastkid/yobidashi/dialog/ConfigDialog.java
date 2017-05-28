/*
 * Copyright (c) 2017 toastkidjp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package jp.toastkid.yobidashi.dialog;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.jfoenix.controls.JFXTextField;

import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Window;
import jp.toastkid.dialog.AlertDialog;
import jp.toastkid.yobidashi.models.Config;
import jp.toastkid.yobidashi.models.Config.Key;
import jp.toastkid.yobidashi.models.Defines;

/**
 * WikiCliend の設定ダイアログ
 * @author Toast kid
 * @see <a href="http://d.hatena.ne.jp/aoe-tk/20130526/1369577773">
 * JavaFX2.2でダイアログを作る方法</a>
 *
 */
public final class ConfigDialog {

    /** Dialog. */
    private final AlertDialog dialog;

    /** author(tool user name). */
    private TextField author;

    /** tool's title. */
    private TextField toolTitle;

    /** tool's icon. */
    private TextField toolIcon;

    /** path/to/article_dir. */
    private TextField articleFolder;

    /** path/to/picture_dir. */
    private TextField pictureFolder;

    /**
     * Read config values from passed path file.
     */
    public final void loadConfig(final Path path) {
        final Config config = new Config(path);
        author        = new JFXTextField(config.get(Key.AUTHOR));
        toolTitle     = new JFXTextField(config.get(Key.APP_TITLE));
        toolIcon      = new JFXTextField(config.get(Key.APP_ICON));
        articleFolder = new JFXTextField(config.get(Key.ARTICLE_DIR));
        pictureFolder = new JFXTextField(config.get(Key.IMAGE_DIR));
    }

    /**
     * Initialize config dialog.
     */
    public ConfigDialog(final Window window) {
        loadConfig(Defines.CONFIG);
        dialog = new AlertDialog.Builder(window)
                .setTitle("Config")
                .setOnPositive("Save", () -> store(Defines.CONFIG))
                .setOnNegative("Cancel", () -> {})
                .addControl(
                        new Label("Author"),         author,
                        new Label("App Title"),      toolTitle,
                        new Label("App Icon"),       toolIcon,
                        new Label("Article folder"), articleFolder,
                        new Label("Picture folder"), pictureFolder
                        )
                .build();
    }

    /**
     * Store values to path.
     */
    public final void store(final Path path) {
        final Map<String,String> newValues = new HashMap<String,String>(10);
        if (StringUtils.isNotEmpty(author.getText())) {
            newValues.put(Key.AUTHOR.text(),      author.getText());
        }
        if (StringUtils.isNotEmpty(toolTitle.getText())) {
            newValues.put(Key.APP_TITLE.text(),   toolTitle.getText());
        }
        if (StringUtils.isNotEmpty(toolIcon.getText())) {
            newValues.put(Key.APP_ICON.text(),    toolIcon.getText());
        }
        if (StringUtils.isNotEmpty(articleFolder.getText())) {
            newValues.put(Key.ARTICLE_DIR.text(), articleFolder.getText());
        }
        if (StringUtils.isNotEmpty(pictureFolder.getText())) {
            newValues.put(Key.IMAGE_DIR.text(),   pictureFolder.getText());
        }
        final Config config = new Config(path);
        config.store(newValues);
        dialog.close();
    }

    /**
     * 入力を受け付けるダイアログを表示する.
     * This method is stopping main thread until when the dialog close.
     * @param title  ダイアログのタイトル
     * @param msg    ダイアログのメッセージ
     * @param defaultInput デフォルト入力、null を指定した場合はテキストフィールドを表示しない
     * @param checkText チェックボックスの文字列、null か空白を指定した時は表示しない
     * @return input 入力文字列
     */
    public final void showAndWait() {
        dialog.showAndWait();
    }

}