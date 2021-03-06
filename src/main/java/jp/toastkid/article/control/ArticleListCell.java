/*
 * Copyright (c) 2017 toastkidjp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package jp.toastkid.article.control;

import com.jfoenix.controls.JFXListCell;

import javafx.scene.control.Label;
import jp.toastkid.article.models.Article;

/**
 * 記事一覧リストのセル.
 * @author Toast kid
 * @see <a href="http://aoe-tk.hatenablog.com/entry/20131206/1386345344">
 * ListViewやTableViewのセルをカスタマイズする方法 (JavaFX Advent Calendar2013 7日目)</a>
 */
public class ArticleListCell extends JFXListCell<Article> {

    /** style class. */
    private static final String LIST_CELL_STYLE_CLASS = "list-cell";

    private boolean bound = false;

    /**
     * init object.
     */
    public ArticleListCell() {
        this.getStyleClass().setAll(LIST_CELL_STYLE_CLASS);
    }

    @Override
    public void updateItem(final Article article, final boolean empty) {
        super.updateItem(article, empty);
        if (!bound) {
            bound = true;
        }

        if (empty) {
            setText(null);
            setGraphic(null);
            return;
        }
        final String text = new StringBuilder()
                .append(article.title).append(System.lineSeparator()).append("Last modified：")
                .append(article.lastModifiedText()).toString();
        ((Label) cellContent).setText(text);
    }

    /**
     * For use testing.
     * @return cell text
     */
    public String cellText() {
        return ((Label) cellContent).getText();
    }

}