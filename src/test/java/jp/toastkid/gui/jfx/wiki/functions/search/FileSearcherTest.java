package jp.toastkid.gui.jfx.wiki.functions.search;

import static org.junit.Assert.assertEquals;

import java.util.Map;

import org.junit.Ignore;

import jp.toastkid.gui.jfx.wiki.models.Config;
import jp.toastkid.gui.jfx.wiki.search.FileSearcher;
import jp.toastkid.gui.jfx.wiki.search.SearchResult;
import jp.toastkid.libs.utils.Strings;

/**
 * ファイル検索クラスのテスト.
 * @author Toast kid
 *
 */
public final class FileSearcherTest {

    /** 中身を検索させるので false. */
    private static final boolean IS_TITLE_ONLY = false;
    /** AND 検索させるので true. */
    private static final boolean IS_AND        = true;
    /** 検索対象クエリ. */
    private static final String  QUERY         = "ラーメン 二郎";

    /**
     * 並列数による検索スピードの違いを検証.
     * <b>普段は実行しない.</b>
     */
    @Ignore
    public final void testSearch() {
        final FileSearcher fs = new FileSearcher.Builder()
            .setHomeDirPath(Config.get("articleDir"))
            .setAnd(IS_AND)
            .setTitleOnly(IS_TITLE_ONLY)
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
    private void search(final FileSearcher fs) {
        final Map<String, SearchResult> map = fs.search(QUERY);
        final StringBuilder searchResult = new StringBuilder(4000);
        // (130302) 検索結果の HTML を生成
        if (!map.isEmpty()) {
            searchResult.append(map.size() + "件のページが見つかりました.<BR>");
            searchResult.append("処理時間：" + fs.getLastSearchTime() + "[ms]／");
            searchResult.append(Strings.LINE_SEPARATOR);
            searchResult.append("検索したファイル数：" + fs.getLastFilenum());
            searchResult.append(Strings.LINE_SEPARATOR);
            searchResult.append("検索結果" + map);

        } else {
            searchResult.append("見つかりませんでした.");
        }
        System.out.println(searchResult.toString());
        System.out.println("検索完了：" + fs.getLastSearchTime() + "[ms]");
    }

    /**
     * 並列数に 0 以下がセットされる時は 1 が強制セットされることを確認.
     */
    @Ignore
    public void setAndGetParallelTest() {
        final FileSearcher fs = new FileSearcher.Builder()
            .setHomeDirPath("").setAnd(false).setTitleOnly(false).build();
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
