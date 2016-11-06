package jp.toastkid.wiki.search;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.impl.factory.Maps;
import org.eclipse.collections.impl.factory.Sets;
import org.eclipse.collections.impl.list.fixed.ArrayAdapter;
import org.eclipse.collections.impl.utility.ArrayIterate;

import javafx.concurrent.Task;
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
public final class FileSearcher {

    /** クエリ区切り文字. */
    public static final String QUERY_DELIMITER = " ";

    /** マルチスレッドで検索する時の並列数 */
    private int parallel = 12;

    /** AND 検索を使用するか否か. */
    private boolean isAnd = false;

    /** 文字列検索の結果を記録する Map.(111231) */
    private Map<String, SearchResult> searchResultMap;

    /** アプリケーションで扱うホームフォルダのパス. */
    private final String homeDirPath;

    /** 最後に検索した時の処理時間.(130302) */
    private long lastSearchTime = 0;

    /** 最後に検索した時のファイル数.(130302) */
    private int lastFilenum = 0;

    /** 記事名のみを検索対象とするか. */
    private boolean isTitleOnly = false;

    /** 検索対象記事名. */
    private final String selectName;

    /**
     * Builder.
     * @author Toast kid
     *
     */
    public static class Builder {

        private String homeDirPath;

        private boolean isAnd;

        private boolean isTitleOnly;

        private String selectName;

        public Builder setHomeDirPath(final String homeDirPath) {
            this.homeDirPath = homeDirPath;
            return this;
        }

        public Builder setAnd(final boolean isAnd) {
            this.isAnd = isAnd;
            return this;
        }

        public Builder setTitleOnly(final boolean isTitleOnly) {
            this.isTitleOnly = isTitleOnly;
            return this;
        }

        public Builder setSelectName(final String selectName) {
            this.selectName = selectName;
            return this;
        }

        public FileSearcher build() {
            return new FileSearcher(this);
        }
    }

    /**
     * 引数 pIsAnd が true の時、AND 検索を実施する.
     * <HR>
     * (130302) homeDirPath をコンストラクタのパラメータに変更<BR>
     * (121124) 作成<BR>
     * @param homeDirPath 検索対象フォルダ
     * @param pIsAnd      AND 検索を実施するか否か
     * @param isTitleOnly 記事名のみを検索対象とするか
     * @param selectName  検索対象とする記事名
     */
    private FileSearcher(final Builder b) {
        this.homeDirPath = b.homeDirPath;
        this.isAnd       = b.isAnd;
        this.isTitleOnly = b.isTitleOnly;
        this.selectName  = b.selectName;
    }

    /**
     * フォルダ内ファイルから文字列を探索する.
     * <HR>
     * (111231) 作成
     */
    public Map<String, SearchResult> search(final String pQuery) {
        final CountDownLatch latch = new CountDownLatch(1);
        final ProgressDialog pd = new ProgressDialog.Builder().setCommand(new Task<Integer>(){
            @Override
            protected Integer call() throws Exception {
                final long start = System.currentTimeMillis();
                lastFilenum      = 0;
                searchResultMap  = Maps.mutable.empty();
                dirSearch(homeDirPath, pQuery);
                // 検索にかかった時間
                lastSearchTime   = System.currentTimeMillis() - start;
                updateMessage("検索完了: " + lastSearchTime + "[ms]");
                latch.countDown();
                return 100;
            }

        }).build();
        pd.start(new Stage());
        try {
            latch.await();
        } catch (final InterruptedException e) {
            e.printStackTrace();
        }
        return searchResultMap;
    }

    /**
     * 再帰的に検索するためのメソッドをマルチスレッドで実装
     * <HR>
     * (140202) 作成
     * @param dirPath フォルダパス
     * @param pQuery  検索クエリ
     */
    private void dirSearch(final String dirPath, final String pQuery) {
        final ExecutorService exs = Executors.newFixedThreadPool(getParallel());
        final Set<Pattern> patterns = convertPatterns(pQuery);
        final File[] files = new File(dirPath).listFiles();
        // 検索の Runnable
        final MutableList<FileSearchTask> runnables = ArrayIterate
                .reject(files, file ->
                    StringUtils.isNotEmpty(this.selectName)
                    && !Article.convertTitle(file.getName()).contains(this.selectName)
                )
                .collect(file -> new FileSearchTask(file.getAbsolutePath(), patterns));
        final List<Future<?>> fList = new ArrayList<Future<?>>();
        // 検索の Runnable を初期化する
        for (int i = 0; i < runnables.size(); i++) {
            final File readingFile = files[i];
            if (readingFile.isDirectory()) {
                dirSearch(readingFile.getAbsolutePath(), pQuery);
                continue;
            }
            lastFilenum++;
            if (!isTitleOnly) {
                fList.add(exs.submit(runnables.get(i)));
                continue;
            }
            // 記事名検索の場合、ここで結果を入れる.
            if (Article.convertTitle(readingFile.getName()).indexOf(pQuery) != -1) {
                searchResultMap.put(
                        readingFile.getName(),
                        SearchResult.makeSimple(readingFile.getAbsolutePath())
                );
            }
        }
        // 記事名検索の時はこれを実施
        if (fList.isEmpty()) {
            return;
        }
        // 内容検索の時は Future を確認
        boolean isEnded = false;
        while (!isEnded) {
            for (final Future<?> f : fList) {
                isEnded = f.isDone();
                if (!isEnded) {
                    continue;
                }
            }
        }
        runnables
            .reject(elem -> elem.getResult().df.isEmpty())
            .reject(elem -> isAnd && elem.getResult().df.size() < patterns.size())
            .each(  elem -> searchResultMap.put(elem.getFilePath(), elem.getResult()));
    }

    /**
     * 検索の並列数を返す.
     * @return 検索の並列数
     */
    public int getParallel() {
        return parallel;
    }

    /**
     * 検索の並列数を設定する.0以下が渡された時は 1 を設定する.
     * @param parallel 検索の並列数
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
        if (!query.contains(QUERY_DELIMITER)) {
            return Sets.fixedSize.of(Pattern.compile(Strings.escapeForRegex(query), Pattern.DOTALL));
        }
        return ArrayAdapter.adapt(query.split(QUERY_DELIMITER))
                .reject(StringUtils::isEmpty)
                .collect(str -> Pattern.compile(Strings.escapeForRegex(str), Pattern.DOTALL))
                .toSet();
    }

}
