package jp.toastkid.wiki.search;

import java.io.File;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Consumer;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.collections.api.block.predicate.Predicate;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.impl.factory.Lists;
import org.eclipse.collections.impl.factory.Maps;
import org.eclipse.collections.impl.factory.Sets;
import org.eclipse.collections.impl.list.fixed.ArrayAdapter;
import org.eclipse.collections.impl.utility.ArrayIterate;

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
import jp.toastkid.dialog.ProgressDialog;
import jp.toastkid.libs.utils.Strings;
import jp.toastkid.wiki.models.Article;

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

    /** Header pane's style. */
    private static final String HEADER_BACKGROUND_STYLE = "-fx-background-color: rgba(244, 244, 245, 0.88);";

    /** Query delimiter. */
    public static final String QUERY_DELIMITER = " ";

    /** マルチスレッドで検索する時の並列数 */
    private int parallel = 12;

    /** AND 検索を使用するか否か. */
    private boolean isAnd = false;

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
            protected Integer call() throws Exception {
                final long start = System.currentTimeMillis();
                lastFilenum      = 0;
                searchResultMap  = Maps.mutable.empty();
                max = 0;
                progress = new SimpleDoubleProperty();
                progress.addListener((prev, next, v) -> {
                    updateProgress(v.doubleValue(), max);
                    updateMessage(String.format("Search in progress... %d / %d", v.intValue(), max));
                });
                dirSearch(dirPath, pQuery);
                // 検索にかかった時間
                lastSearchTime   = System.currentTimeMillis() - start;
                makeResultTab(pQuery);
                final String message = "Done：" + lastSearchTime + "[ms]";
                updateMessage(message);
                successAction.accept(message);
                updateProgress(100, 100);
                return 100;
            }

        }).build();
        pd.start(new Stage());
    }

    /**
     * 再帰的に検索するためのメソッドをマルチスレッドで実装
     * <HR>
     * (140202) 作成
     * @param dirPath フォルダパス
     * @param pQuery  検索クエリ
     * @param prog
     */
    private void dirSearch(final String dirPath, final String pQuery) {
        final ExecutorService exs = Executors.newFixedThreadPool(getParallel());
        final Set<Pattern> patterns = convertPatterns(pQuery);
        final boolean isTitleOnly = patterns.isEmpty();
        final File[] files = new File(dirPath).listFiles();
        // 検索の Runnable
        final MutableList<ArticleSearchTask> runnables = ArrayIterate
                .reject(files, file ->
                    StringUtils.isNotEmpty(this.selectName)
                    && !Article.convertTitle(file.getName()).contains(this.selectName)
                )
                .collect(file -> new ArticleSearchTask(file.getAbsolutePath(), patterns));
        max = max + runnables.size();
        final MutableList<Future<?>> futures = Lists.mutable.empty();
        // 検索の Runnable を初期化する
        for (int i = 0; i < runnables.size(); i++) {
            final File readingFile = files[i];
            if (readingFile.isDirectory()) {
                dirSearch(readingFile.getAbsolutePath(), pQuery);
                continue;
            }
            lastFilenum++;
            if (!isTitleOnly) {
                futures.add(exs.submit(runnables.get(i)));
                continue;
            }
            // 記事名検索の場合、ここで結果を入れる.
            if (Article.convertTitle(readingFile.getName()).contains(this.selectName)) {
                searchResultMap.put(
                        readingFile.getName(),
                        SearchResult.makeSimple(readingFile.getAbsolutePath())
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
        final int size = patterns.size();
        runnables
            .reject(invalidElement(size))
            .each(  elem -> searchResultMap.put(elem.getFilePath(), elem.getResult()));
        if (searchResultMap.isEmpty()) {
            emptyAction.run();
            return;
        }
    }

    /**
     * Check invalid result.
     * @param size query num.
     * @return If it's invalid result, true
     */
    private Predicate<ArticleSearchTask> invalidElement(final int size) {
        return elem -> elem.getResult().df.isEmpty()
                && isAnd
                && elem.getResult().df.size() < size;
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
                    .map(entry -> new Article(new File(entry.getValue().filePath)))
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
            return Sets.fixedSize.of(Pattern.compile(Strings.escapeForRegex(query), Pattern.DOTALL));
        }
        return ArrayAdapter.adapt(query.split(QUERY_DELIMITER))
                .reject(StringUtils::isEmpty)
                .collect(str -> Pattern.compile(Strings.escapeForRegex(str), Pattern.DOTALL))
                .toSet();
    }

}
