/*
 * Copyright (c) 2017 toastkidjp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package jp.toastkid.yobidashi.models;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.stage.Window;
import jp.toastkid.article.models.Article;
import jp.toastkid.dialog.AlertDialog;

/**
 * Bookmark manager.
 *
 * @author Toast kid
 */
public class BookmarkManager {

    /** Logger. */
    private static final Logger LOGGER = LoggerFactory.getLogger(BookmarkManager.class);

    /** path/to/bookmark. */
    private final Path path;

    /** Bookmark content area. */
    private final CodeArea bookmarks;

    /** Dialog. */
    private AlertDialog dialog;

    /**
     * Constructor.
     */
    public BookmarkManager(final Path path) {
        this.path = path;
        bookmarks = new CodeArea();
        bookmarks.setParagraphGraphicFactory(LineNumberFactory.get(bookmarks));
    }

    /**
     * Edit bookmarks.
     *
     * @param parent This dialog's parent window
     */
    public void edit(final Window parent) {
        readBookmark();

        if (dialog == null) {
            dialog = new AlertDialog.Builder(parent)
                    .setTitle("Edit Bookmark")
                    .setMessage("Would you input article titles.")
                    .addControl(bookmarks)
                    .setOnPositive("Store", () -> store(bookmarks.getText()))
                    .build();
        }
        dialog.show();
    }

    /**
     * Store content to file.
     * @param newContent
     */
    private void store(final String newContent) {
        try {
            Files.write(path, newContent.getBytes(Article.ENCODE));
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Read bookmark list.
     */
    private void readBookmark() {
        if (!Files.exists(path)) {
            return;
        }
        try {
            final String currentContent = Files.readAllLines(path).stream()
                    .collect(Collectors.joining(System.lineSeparator()));
            bookmarks.replaceText(currentContent);
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Read content with MutableList.
     * @return
     */
    public List<String> readLines() {
        try {
            return Files.readAllLines(path);
        } catch (final IOException e) {
            LOGGER.error("Error!", e);
        }
        return Collections.emptyList();
    }

}
