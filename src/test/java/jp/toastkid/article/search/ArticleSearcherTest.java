package jp.toastkid.article.search;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.net.URISyntaxException;
import java.nio.file.Paths;

import org.junit.Test;
import org.testfx.framework.junit.ApplicationTest;

import javafx.application.Platform;
import javafx.scene.control.TabPane;
import javafx.stage.Stage;

/**
 * ファイル検索クラスのテスト.
 * @author Toast kid
 *
 */
public final class ArticleSearcherTest extends ApplicationTest {

    /** AND 検索させるので true. */
    private static final boolean IS_AND = true;

    /** 検索対象クエリ. */
    private static final String  QUERY  = "ラーメン 二郎";

    /**
     * 並列数による検索スピードの違いを検証.
     * <b>普段は実行しない.</b>
     * @throws URISyntaxException
     */
    @Test
    public final void testSearch() throws URISyntaxException {
        final ArticleSearcher fs = new ArticleSearcher.Builder()
            .setHomeDirPath(Paths.get(getClass().getClassLoader().getResource("chart").toURI()).toString())
            .setAnd(IS_AND)
            .setEmptyAction(() -> {})
            .setSuccessAction(str -> assertTrue(true))
            .setTabPane(new TabPane())
            .setListViewInitializer(lv -> {})
            .build();
        fs.setParallel(5);
        Platform.runLater(() -> search(fs));
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
    @Test
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

    @Override
    public void start(Stage stage) throws Exception {
        // NOP.
    }
}
