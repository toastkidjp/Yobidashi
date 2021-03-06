/*
 * Copyright (c) 2017 toastkidjp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package jp.toastkid.article.search;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jfoenix.controls.JFXListView;

import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import jp.toastkid.article.models.Article;
import jp.toastkid.article.models.Articles;
import jp.toastkid.dialog.ProgressDialog;
import jp.toastkid.libs.utils.Strings;

/**
 * フォルダ単位での文字列検索を実施するクラス
 * <HR>
 * (130302) homeDirPath をコンストラクタのパラメータに変更<BR>
 * (121124) AND 検索実装<BR>
 * (121124) package gui.wikilike; の Functions から分離<BR>
 *
 * @author Toast kid
 *
 */
public final class ArticleSearcher {

    /** Logger. */
    private static final Logger LOGGER = LoggerFactory.getLogger(ArticleSearcher.class);

    /** Header pane's style. */
    private static final String HEADER_BACKGROUND_STYLE = "-fx-background-color: rgba(244, 244, 245, 0.88);";

    /** Query delimiter. */
    public static final String QUERY_DELIMITER = " ";

    /** マルチスレッドで検索する時の並列数 */
    private int parallel = 12;

    /** AND 検索を使用するか否か. */
    private final boolean isAnd;

    /** Path string of article dir. */
    private final String dirPath;

    /** Use by filtering articles. */
    private final String selectName;

    /** TabPane of inserting result tab. */
    private final TabPane leftTabs;

    /** ListView's size. */
    private final double height;

    /** Action of empty result. */
    private final Runnable emptyAction;

    /** Action of initialize ListView. */
    private final Consumer<ListView<Article>> initializer;

    /** Action of success. */
    private final Consumer<String> successAction;

    /** 文字列検索の結果を記録する Map.(111231) */
    private Map<String, SearchResult> searchResultMap;

    /** 最後に検索した時の処理時間.(130302) */
    private long lastSearchTime = 0;

    /** 最後に検索した時のファイル数.(130302) */
    private int lastFilenum = 0;

    /** Progress value. */
    private DoubleProperty progress;

    /** Article num. */
    private int max;

    /**
     * Builder.
     * @author Toast kid
     *
     */
    public static class Builder {

        private String homeDirPath;

        private boolean isAnd;

        private String selectName;

        private Runnable emptyAction;

        private TabPane leftTabs;

        private Consumer<ListView<Article>> initializer;

        private Consumer<String> successAction;

        private double height;

        public Builder setHomeDirPath(final String homeDirPath) {
            this.homeDirPath = homeDirPath;
            return this;
        }

        public Builder setAnd(final boolean isAnd) {
            this.isAnd = isAnd;
            return this;
        }

        public Builder setSelectName(final String selectName) {
            this.selectName = selectName;
            return this;
        }

        public Builder setTabPane(final TabPane leftTabs) {
            this.leftTabs = leftTabs;
            return this;
        }

        public Builder setListViewInitializer(final Consumer<ListView<Article>> initializer) {
            this.initializer = initializer;
            return this;
        }

        public Builder setHeight(final double height) {
            this.height = height;
            return this;
        }

        public Builder setEmptyAction(final Runnable emptyAction) {
            this.emptyAction = emptyAction;
            return this;
        }

        public Builder setSuccessAction(final Consumer<String> successAction) {
            this.successAction = successAction;
            return this;
        }

        public ArticleSearcher build() {
            return new ArticleSearcher(this);
        }

    }

    /**
     * 引数 pIsAnd が true の時、AND 検索を実施する.
     * @param b Builder
     */
    private ArticleSearcher(final Builder b) {
        this.dirPath       = b.homeDirPath;
        this.isAnd         = b.isAnd;
        this.selectName    = b.selectName;
        this.emptyAction   = b.emptyAction;
        this.leftTabs      = b.leftTabs;
        this.initializer   = b.initializer;
        this.height        = b.height;
        this.successAction = b.successAction;
    }

    /**
     * Search string from file.
     */
    public void search(final String pQuery) {
        final ProgressDialog pd = new ProgressDialog.Builder().setCommand(new Task<Integer>(){

            @Override
            protected Integer call() {
                try {
                    final long start = System.currentTimeMillis();
                    lastFilenum      = 0;
                    searchResultMap  = new HashMap<>();
                    max = 0;
                    progress = new SimpleDoubleProperty();
                    progress.addListener((prev, next, v) -> {
                        updateProgress(v.doubleValue(), max);
                        updateMessage(String.format("Search in progress... %d / %d", v.intValue(), max));
                    });
                    dirSearch(dirPath, pQuery);
                    // 検索にかかった時間
                    lastSearchTime   = System.currentTimeMillis() - start;
                    if (!searchResultMap.isEmpty()) {
                        makeResultTab(pQuery);
                    }
                    final String message = "Done：" + lastSearchTime + "[ms]";
                    updateMessage(message);
                    successAction.accept(message);
                    updateProgress(100, 100);
                    done();
                } catch (final Exception e) {
                    LOGGER.error("Caught exception.", e);
                    updateProgress(100, 100);
                    failed();
                }
                return 100;
            }

        }).build();
        pd.start(new Stage());
    }

    /**
     * 再帰的に検索するためのメソッドをマルチスレッドで実装
     * <HR>
     * (140202) 作成
     * @param dirPath directory path
     * @param query   search query
     */
    private void dirSearch(final String dirPath, final String query) {
        final ExecutorService exs = Executors.newFixedThreadPool(getParallel());
        final Set<Pattern> patterns = convertPatterns(query);
        final boolean isTitleOnly = patterns.isEmpty();

        final List<Path> paths = new ArrayList<>();
        try {
            Files.list(Paths.get(dirPath)).forEach(paths::add);
        } catch (final IOException e) {
            e.printStackTrace();
            LOGGER.error("Error!", e);
        }

        // 検索の Runnable
        final List<ArticleSearchTask> runnables = paths
                .stream()
                .filter(path ->
                    StringUtils.isEmpty(this.selectName)
                    || Articles.convertTitle(path.getFileName().toString()).contains(this.selectName)
                )
                .map(path -> new ArticleSearchTask(path.toAbsolutePath().toString(), patterns))
                .collect(Collectors.toList());
        max = max + runnables.size();
        final List<Future<?>> futures = new ArrayList<>();
        // 検索の Runnable を初期化する
        for (int i = 0; i < runnables.size(); i++) {
            final Path readingPath = paths.get(i);
            if (Files.isDirectory(readingPath)) {
                dirSearch(readingPath.toAbsolutePath().toString(), query);
                continue;
            }
            lastFilenum++;
            if (!isTitleOnly) {
                futures.add(exs.submit(runnables.get(i)));
                continue;
            }
            // 記事名検索の場合、ここで結果を入れる.
            if (Articles.convertTitle(readingPath).contains(this.selectName)) {
                searchResultMap.put(
                        readingPath.getFileName().toString(),
                        SearchResult.makeSimple(readingPath.toAbsolutePath().toString())
                );
                progress.set(progress.get() + 1.0);
            }
        }
        // 内容検索の時は Future を確認
        while (!futures.isEmpty()) {
            for (final Iterator<Future<?>> iter = futures.iterator(); iter.hasNext();) {
                final Future<?> f = iter.next();
                if (f.isDone()) {
                    progress.set(progress.get() + 1.0);
                    iter.remove();
                    continue;
                }
                break;
            }
        }
        final int patternNum = patterns.size();
        runnables
            .stream()
            .filter(validElement(patternNum))
            .forEach(elem -> searchResultMap.put(elem.getFilePath(), elem.getResult()));
        if (searchResultMap.isEmpty()) {
            Platform.runLater(emptyAction::run);
        }
    }

    /**
     * Check valid result.
     * @param size query num.
     * @return If it's valid result, true
     */
    private Predicate<ArticleSearchTask> validElement(final int size) {
        return elem -> {
            final SearchResult result = elem.getResult();
            if (result.mapIsEmpty()) {
                return false;
            }
            if (!isAnd) {
                return true;
            }
            return result.size() <= size;
        };
    }

    /**
     * Make result list tab.
     * @param query search query
     */
    private void makeResultTab(final String query) {
        final Tab tab = makeClosableTab(
                String.format("「%s」's %s search result", query, isAnd ? "AND" : "OR"),
                leftTabs
                );
        // prepare tab's content.
        final VBox box = new VBox();
        final ObservableList<Node> children = box.getChildren();
        children.add(makeSearchHeaderLable(
                String.format("Search time: %d[ms]", getLastSearchTime())));
        children.add(makeSearchHeaderLable(
                String.format("%dFiles / %dFiles", searchResultMap.size(), getLastFilenum())));
        // set up ListView.
        final ListView<Article> listView = new JFXListView<>();
        listView.getStyleClass().add("left-tabs");
        initializer.accept(listView);
        listView.getItems().addAll(
                searchResultMap.entrySet().stream()
                    .map(entry -> new Article(Paths.get(entry.getValue().filePath())))
                    .sorted()
                    .collect(Collectors.toList())
                );
        listView.setMinHeight(height + box.getHeight());

        children.add(listView);
        tab.setContent(box);
        Platform.runLater(() -> leftTabs.getTabs().add(tab));
        leftTabs.getSelectionModel().select(tab);
    }

    /**
     * Make Label with white background.
     * @param text showing text.
     * @return text label
     */
    private Label makeSearchHeaderLable(final String text) {
        final Label header = new Label(text);
        header.setStyle(HEADER_BACKGROUND_STYLE);
        return header;
    }

    /**
     * Make empty closable tab.
     * @param title Tab's title
     * @param parent Parent TabPane
     * @return empty Tab
     */
    private static Tab makeClosableTab(final String title, final TabPane parent) {
        final Tab tab = new Tab(title);
        tab.setClosable(true);

        final Button closeButton = new Button("x");
        closeButton.setOnAction(e -> parent.getTabs().remove(tab));
        tab.setGraphic(closeButton);
        return tab;
    }

    /**
     * Return search parallelism number.
     * @return Search parallelism number
     */
    public int getParallel() {
        return parallel;
    }

    /**
     * Set search parallelism number. 0以下が渡された時は 1 を設定する.
     * @param parallel Search parallelism number
     */
    public void setParallel(final int parallel) {
        this.parallel = (0 < parallel) ? parallel : 1;
    }

    /**
     * 最後に検索した時の処理時間を取得する.
     * <HR>
     * @return 最後に検索した時の処理時間(long値)
     * (130302) 作成<BR>
     */
    public long getLastSearchTime() {
        return lastSearchTime;
    }

    /**
     * 最後に検索した時のファイル数を取得する.
     * <HR>
     * @return 最後に検索した時のファイル数(int値)
     * (130302) 作成<BR>
     */
    public int getLastFilenum() {
        return lastFilenum;
    }

    /**
     * 検索パターンの一覧を生成して返す.
     * 検索クエリを半角スペースで区切る.
     * @param query 検索クエリ、QUERY_DELIMITER 区切りで複数指定可能
     */
    private Set<Pattern> convertPatterns(final String query) {

        if (StringUtils.isEmpty(query)) {
            return Collections.emptySet();
        }

        if (!query.contains(QUERY_DELIMITER)) {
            return new HashSet<Pattern>(){{add(Pattern.compile(Strings.escapeForRegex(query), Pattern.DOTALL));}};
        }
        return Stream.of(query.split(QUERY_DELIMITER))
                .filter(StringUtils::isNotEmpty)
                .map(str -> Pattern.compile(Strings.escapeForRegex(str), Pattern.DOTALL))
                .collect(Collectors.toSet());
    }

}
