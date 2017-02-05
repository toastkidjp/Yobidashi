package jp.toastkid.wordcloud;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.collections.api.RichIterable;
import org.eclipse.collections.api.map.MutableMap;
import org.eclipse.collections.api.tuple.Pair;
import org.eclipse.collections.impl.factory.Maps;

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

    /** number of words in drawing. */
    private final int    numOfWords;

    /** minimum font size./ */
    private final double minFontSize;

    /** maximum font size, */
    private final double maxFontSize;

    public static class Builder {
        /** number of words in drawing. */
        private int    numOfWords  = NUMBER_OF_WORDS;

        /** minimum font size./ */
        private double minFontSize = MIN_FONT_SIZE;

        /** maximum font size, */
        private double maxFontSize = MAX_FONT_SIZE;

        public Builder setNumOfWords(final int numOfWords) {
            this.numOfWords = numOfWords;
            return this;
        }

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
     * call from only internal.
     * @param b Builder
     */
    private FxWordCloud(final Builder b) {
        this.numOfWords  = b.numOfWords  < 1 ? NUMBER_OF_WORDS : b.numOfWords;
        this.minFontSize = b.minFontSize < 1.0 ? MIN_FONT_SIZE : b.minFontSize;
        this.maxFontSize = b.maxFontSize < 1.0 ? MAX_FONT_SIZE : b.maxFontSize;
    }

    /**
     * draw Word Cloud on passed Pane.
     * @param canvas
     * @param pathToFile path/to/file
     */
    public void draw(final Pane canvas, final String pathToFile) {
        draw(canvas, Paths.get(pathToFile));
    }

    /**
     * draw Word Cloud on passed Pane.
     * @param canvas
     * @param path Path
     */
    public void draw(final Pane canvas, final Path path) {
        final TinySegmenter ts = TinySegmenter.getInstance();
        ts.isAllowChar     = false;
        ts.isAllowHiragana = false;
        ts.isAllowNum      = false;
        final MutableMap<String, Integer> map = Maps.mutable.empty();
        FileUtil.readLines(path, Article.ENCODE)
            .stream()
            .filter(StringUtils::isNotEmpty)
            .forEach(str -> {
                ts.segment(str).stream()
                    .map(seg -> seg.replace("\"", ""))
                    .filter(StringUtils::isNotEmpty)
                    .forEach(seg -> map.put(seg, map.getIfAbsentValue(seg, 0) + 1));
        });
        placeTexts(canvas, map);
    }

    /**
     * draw Word Cloud on passed Pane.
     * @param canvas
     * @param path file
     */
    public void draw(final Pane canvas, final Map<String, Integer> map) {
        final MutableMap<String, Integer> m = Maps.mutable.empty();
        m.putAll(map);
        placeTexts(canvas, m);
    }

    /**
     * place texts to passed pane.
     * @param canvas pane
     * @param map map
     */
    private void placeTexts(final Pane canvas, final MutableMap<String, Integer> map) {
        final RichIterable<Pair<String, Integer>> iter = map.keyValuesView()
            .toSortedMap((t1, t2) ->
                Integer.compare(t1.getTwo(), t2.getTwo()), key -> key, value -> value)
            ;//.drop(numOfWords);
        final int max = iter.collectInt(Pair::getTwo).max();
        // System.out.println(max + " " + iter.toList().toString());
        canvas.getChildren().addAll(
                iter.collect(pair -> makeLabel(max, pair)).toList().shuffleThis());
    }

    /**
     * make Label object with Tooltip.
     * @param max max value
     * @param pair word and count.
     * @return Label
     */
    private Label makeLabel(final int max, final Pair<String, Integer> pair) {
        final String k = pair.getOne();
        final int v = pair.getTwo();
        final Label text = new Label(k);
        final Font font
            = new Font("Roboto", Math.max(maxFontSize * ((double) v / (double) max), minFontSize));
        text.setFont(font);
        text.setTextFill(Color.color(Math.random(), Math.random(), Math.random()));
        text.setStyle("fx-font-weight: bold;");
        Tooltip.install(text, new Tooltip(k + ": " + v));
        return text;
    }
}
