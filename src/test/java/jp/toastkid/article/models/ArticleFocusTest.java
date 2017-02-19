package jp.toastkid.article.models;

import static org.junit.Assert.assertEquals;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Test;
import org.testfx.framework.junit.ApplicationTest;

import javafx.scene.control.ListView;
import javafx.stage.Stage;

/**
 * {@link Article}'s test with UI.
 *
 * @author Toast kid
 *
 */
public class ArticleFocusTest extends ApplicationTest {

    /** Test resource Path. */
    private static final Path PATH
        = Paths.get("src/test/resources/article/C6FCB5AD323031332D30382D333128C5DA29.md");

    /** Testing object. */
    private Article a;

    /** ListView. */
    private ListView<Article> listView;

    /**
     * initialize before each test.
     * @throws Exception
     */
    @Override
    public void init() throws Exception {
        super.init();
        a = new Article(PATH);
        listView = new ListView<Article>();
    }

    /**
     * Test of {@link Article#focus(ListView)}.
     */
    @Test
    public void test_focus() {
        a.focus(listView);
        assertEquals(-1, listView.getSelectionModel().getSelectedIndex());

        listView.getItems().add(a);
        a.focus(listView);
        assertEquals(0, listView.getSelectionModel().getSelectedIndex());
    }

    @Override
    public void start(Stage stage) throws Exception {
        init();
    }

}
