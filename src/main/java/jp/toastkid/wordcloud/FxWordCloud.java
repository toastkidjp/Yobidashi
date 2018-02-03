/*
 * Copyright (c) 2017 toastkidjp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package jp.toastkid.wordcloud;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;

import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import jp.toastkid.article.models.Article;
import jp.toastkid.libs.tinysegmenter.TinySegmenter;
import jp.toastkid.libs.utils.FileUtil;

/**
 * Utility of word cloud for JavaFX.
 *
 * @author Toast kid
 * @see <a href="http://stackoverflow.com/questions/11481482/
 *how-can-i-generate-a-tag-cloud-in-java-with-opencloud">
 * How can I generate a tag cloud in Java, with OpenCloud?</a>
 *
 */
public final class FxWordCloud {

    /** default number of words in drawing. */
    public static final int NUMBER_OF_WORDS  = 100;

    /** default minimum font size./ */
    public static final double MIN_FONT_SIZE = 10.0;

    /** default maximum font size, */
    public static final double MAX_FONT_SIZE = 96.0;

    /** minimum font size./ */
    private final double minFontSize;

    /** maximum font size, */
    private final double maxFontSize;

    /**
     * Builder of {@link FxWordCloud}.
     *
     * @author Toast kid
     *
     */
    public static class Builder {

        /** minimum font size./ */
        private double minFontSize = MIN_FONT_SIZE;

        /** maximum font size, */
        private double maxFontSize = MAX_FONT_SIZE;

        public Builder setMinFontSize(final double minFontSize) {
            this.minFontSize = minFontSize;
            return this;
        }

        public Builder setMaxFontSize(final double maxFontSize) {
            this.maxFontSize = maxFontSize;
            return this;
        }

        public FxWordCloud build() {
            return new FxWordCloud(this);
        }
    }

    /**
     * Call from only internal Builder.
     * @param b Builder
     */
    private FxWordCloud(final Builder b) {
        this.minFontSize = b.minFontSize < 1.0 ? MIN_FONT_SIZE : b.minFontSize;
        this.maxFontSize = b.maxFontSize < 1.0 ? MAX_FONT_SIZE : b.maxFontSize;
    }

    /**
     * Draw Word Cloud on passed Pane.
     * @param canvas
     * @param pathToFile path/to/file
     */
    public void draw(final Pane canvas, final String pathToFile) {
        draw(canvas, Paths.get(pathToFile));
    }

    /**
     * Draw Word Cloud on passed Pane.
     * @param canvas
     * @param path Path
     */
    public void draw(final Pane canvas, final Path path) {
        final TinySegmenter ts = TinySegmenter.getInstance();
        ts.isAllowChar     = false;
        ts.isAllowHiragana = false;
        ts.isAllowNum      = false;
        final Map<String, Integer> map = new HashMap<>();
        FileUtil.readLines(path, Article.ENCODE)
        .stream()
        .filter(StringUtils::isNotEmpty)
        .forEach(str -> ts.segment(str).stream()
                        .map(seg -> seg.replace("\"", ""))
                        .filter(StringUtils::isNotEmpty)
                        .forEach(seg -> map.put(seg, map.getOrDefault(seg, 0) + 1)));
        placeTexts(canvas, map);
    }

    /**
     * Place texts to passed pane.
     * @param canvas pane
     * @param map map
     */
    private void placeTexts(final Pane canvas, final Map<String, Integer> map) {
        if (map.isEmpty()) {
            System.out.println("Map is empty.");
            return;
        }

        final int max = Collections.max(map.values());
        final ObservableList<Node> labels = canvas.getChildren();
        final List<Entry<String, Integer>> entries = new ArrayList<>(map.entrySet());
        Collections.shuffle(entries);
        entries.stream().map(pair -> makeLabel(max, pair)).forEach(labels::add);
    }

    /**
     * Make Label object with Tooltip.
     * @param max max value
     * @param pair word and count.
     * @return Label
     */
    private Label makeLabel(final int max, final Entry<String, Integer> pair) {
        final String k = pair.getKey();
        final int    v = pair.getValue();
        final Label text = new Label(k);
        final Font  font = new Font("Roboto", Math.max(maxFontSize * ((double) v / (double) max), minFontSize));
        text.setFont(font);
        text.setTextFill(Color.color(Math.random(), Math.random(), Math.random()));
        text.setStyle("fx-font-weight: bold;");
        Tooltip.install(text, new Tooltip(k + ": " + v));
        return text;
    }
}
