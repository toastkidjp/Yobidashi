package jp.toastkid.article.search;

import static org.junit.Assert.assertEquals;

import org.junit.Ignore;

import jp.toastkid.yobidashi.models.Config;
import jp.toastkid.yobidashi.models.Defines;

/**
 * ファイル検索クラスのテスト.
 * @author Toast kid
 *
 */
public final class ArticleSearcherTest {

    /** AND 検索させるので true. */
    private static final boolean IS_AND = true;

    /** 検索対象クエリ. */
    private static final String  QUERY  = "ラーメン 二郎";

    /**
     * 並列数による検索スピードの違いを検証.
     * <b>普段は実行しない.</b>
     */
    @Ignore
    public final void testSearch() {
        final Config conf = new Config(Defines.CONFIG);
        final ArticleSearcher fs = new ArticleSearcher.Builder()
            .setHomeDirPath(conf.get("articleDir"))
            .setAnd(IS_AND)
            .build();
        fs.setParallel(5);
        search(fs);
        fs.setParallel(10);
        search(fs);
        fs.setParallel(20);
        search(fs);
    }

    /**
     * 渡された FileSearcher で検索を実行する.
     * @param fs
     */
    private void search(final ArticleSearcher fs) {
        fs.search(QUERY);
    }

    /**
     * 並列数に 0 以下がセットされる時は 1 が強制セットされることを確認.
     */
    @Ignore
    public void setAndGetParallelTest() {
        final ArticleSearcher fs = new ArticleSearcher.Builder()
            .setHomeDirPath("").setAnd(false).build();
        fs.setParallel(-2);
        assertEquals(1, fs.getParallel());
        fs.setParallel(-1);
        assertEquals(1, fs.getParallel());
        fs.setParallel(0);
        assertEquals(1, fs.getParallel());
        fs.setParallel(1);
        assertEquals(1, fs.getParallel());
        fs.setParallel(2);
        assertEquals(2, fs.getParallel());
    }
}
