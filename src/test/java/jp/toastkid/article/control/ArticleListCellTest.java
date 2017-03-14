package jp.toastkid.article.control;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.file.Paths;

import org.junit.Test;
import org.testfx.framework.junit.ApplicationTest;

import javafx.stage.Stage;
import jp.toastkid.article.models.Article;

/**
 * {@link ArticleListCell}'s test.
 *
 * @author Toast kid
 *
 */
public class ArticleListCellTest extends ApplicationTest {

    /**
     * Test of {@link ArticleListCell#updateItem(Article, boolean)}.
     * @throws IOException
     */
    @Test
    public void testUpdateItemArticleBoolean() throws IOException {
        final ArticleListCell cell = new ArticleListCell();
        final Article article = new Article(Paths.get("src/test/resources/article/C6FCB5AD323031332D30382D333128C5DA29.md"));
        cell.updateItem(article, false);
        assertTrue(cell.cellText().startsWith("日記2013-08-31(土)"));
        assertNull(cell.getText());

        // for test coverage,
        cell.updateItem(article, true);
    }

    @Override
    public void start(Stage stage) throws Exception {
        // NOP.
    }

}
