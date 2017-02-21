package jp.toastkid.yobidashi;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.function.Consumer;

import org.junit.Test;
import org.mockito.internal.util.reflection.Whitebox;
import org.testfx.framework.junit.ApplicationTest;

import com.jfoenix.controls.JFXTabPane;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import jp.toastkid.article.models.ContentType;
import jp.toastkid.jfx.common.control.MenuLabel;
import jp.toastkid.yobidashi.message.ApplicationMessage;
import jp.toastkid.yobidashi.message.ArticleMessage;
import jp.toastkid.yobidashi.message.Message;
import jp.toastkid.yobidashi.message.ShowSearchDialog;
import jp.toastkid.yobidashi.message.SnackbarMessage;
import jp.toastkid.yobidashi.message.TabMessage;
import jp.toastkid.yobidashi.message.WebTabMessage;
import jp.toastkid.yobidashi.models.Config;
import reactor.core.publisher.TopicProcessor;

/**
 * {@link SideMenuController}'s test case.
 *
 * @author Toast kid
 *
 */
public class SideMenuControllerTest extends ApplicationTest {

    /** Test object. */
    private SideMenuController controller;

    /** Messenger. */
    private TopicProcessor<Message> messenger;

    /** Tab pane. */
    private JFXTabPane tabPane;

    /** Test stage. */
    private Stage stage;

    /**
     * Test of {@link SideMenuController#quit}.
     */
    @Test
    public void test_quit() {
        Platform.runLater(() -> {
            subscribe(m -> {
                final ApplicationMessage am = (ApplicationMessage) m;
                assertEquals(ApplicationMessage.Command.QUIT, am.getCommand());
            });
            selectTab(0);
            fireLabel("Quit");
        });
    }

    /**
     * Test of {@link SideMenuController#fullScreen()}.
     */
    @Test
    public void test_fullScreen() {
        Platform.runLater(() -> {
            selectTab(4);
            final String labelText = "Full screen	|	F11";
            fireLabel(labelText);
            assertTrue(stage.isFullScreen());
            fireLabel(labelText);
            assertFalse(stage.isFullScreen());
        });
    }

    /**
     * Test of {@link SideMenuController#reload()}.
     */
    @Test
    public void test_reload() {
        Platform.runLater(() -> {
            subscribe(m -> {
                final TabMessage tm = (TabMessage) m;
                assertEquals(TabMessage.Command.RELOAD, tm.getCommand());
            });
            selectTab(4);
            fireLabel("Reload", "F5");
        });
    }

    /**
     * Test of {@link SideMenuController#callApplicationState()}.
     */
    @Test
    public void test_callApplicationState() {
        Platform.runLater(() -> {
            selectTab(5);
            fireLabel("Application state");
            type(KeyCode.ESCAPE);
            System.out.println("typed");
        });
    }

    /**
     * Test of {@link SideMenuController#callHtmlSource()}.
     */
    @Test
    public void test_callHtmlSource() {
        Platform.runLater(() -> {
            subscribe(m -> {
                final TabMessage tm = (TabMessage) m;
                assertEquals(TabMessage.Command.PREVIEW, tm.getCommand());
            });
            selectTab(1);
            fireLabel("HTML source", "Ctrl+U");
        });
    }

    /**
     * Test of {@link SideMenuController#closeAllTabs()}.
     */
    @Test
    public void test_closeAllTabs() {
        Platform.runLater(() -> {
            subscribe(m -> {
                final TabMessage tm = (TabMessage) m;
                assertEquals(TabMessage.Command.CLOSE_ALL, tm.getCommand());
            });
            selectTab(0);
            fireLabel("Close all tabs", "Alt+W");
        });
    }

    /**
     * Test of {@link SideMenuController#closeTab()}.
     */
    @Test
    public void test_closeTab() {
        Platform.runLater(() -> {
            subscribe(m -> {
                final TabMessage tm = (TabMessage) m;
                assertEquals(TabMessage.Command.CLOSE, tm.getCommand());
            });
            selectTab(0);
            fireLabel("Close tab", "Ctrl+W");
        });
    }

    /**
     * Test of {@link SideMenuController#openSpeedDialTab()}.
     */
    @Test
    public void test_openSpeedDialTab() {
        Platform.runLater(() -> {
            subscribe(m -> {
                final TabMessage tm = (TabMessage) m;
                assertEquals(TabMessage.Command.OPEN, tm.getCommand());
            });
            selectTab(0);
            fireLabel("Open new tab", "Ctrl+T");
        });
    }

    /**
     * Test of {@link SideMenuController#license()}.
     */
    @Test
    public void test_license() {
        Platform.runLater(() -> {
            subscribe(m -> {
                final WebTabMessage wm = (WebTabMessage) m;
                assertEquals("License", wm.getTitle());
                assertNotNull(wm.getContent());
                assertEquals(ContentType.TEXT, wm.getContentType());
            });
            selectTab(5);
            fireLabel("License");
        });
    }

    /**
     * Test of {@link SideMenuController#callConvertAobun}.
     */
    @Test
    public void testCallConvertAobun() {
        Platform.runLater(() -> {
            final Consumer<? super Message> consumer = m -> {
                final ArticleMessage am = (ArticleMessage) m;
                assertEquals(ArticleMessage.Command.CONVERT_AOBUN, am.getCommand());
            };
            subscribe(consumer);
            selectTab(1);
            fireLabel("Convert current article to AozoraBunko Text");
        });
    }

    /**
     * Test of {@link SideMenuController#callConvertEpub}.
     */
    @Test
    public void testCallConvertEpub() {
        Platform.runLater(() -> {
            final Consumer<? super Message> consumer = m -> {
                final ArticleMessage am = (ArticleMessage) m;
                assertEquals(ArticleMessage.Command.CONVERT_EPUB, am.getCommand());
            };
            subscribe(consumer);
            selectTab(1);
            fireLabel("Convert current article to ePub");
        });
    }

    /**
     * Test of {@link SideMenuController#callWordCloud}.
     */
    @Test
    public void test_callWordCloud() {
        Platform.runLater(() -> {
            subscribe(m -> {
                final ArticleMessage am = (ArticleMessage) m;
                assertEquals(ArticleMessage.Command.WORD_CLOUD, am.getCommand());
            });
            selectTab(3);
            fireLabel("Word cloud", "Shift+W");
        });
    }

    /**
     * Test of {@link SideMenuController#callSearch}.
     */
    @Test
    public void test_callSearch() {
        Platform.runLater(() -> {
            subscribe(m -> {
                final ShowSearchDialog am = (ShowSearchDialog) m;
                assertNotNull(am);
            });
            selectTab(1);
            fireLabel("Article Search", "Ctrl+Shift+F");
        });
    }

    /**
     * Test of {@link SideMenuController#saveArticle}.
     */
    @Test
    public void test_saveArticle() {
        Platform.runLater(() -> {
            subscribe(m -> {
                final TabMessage tm = (TabMessage) m;
                assertEquals(TabMessage.Command.SAVE, tm.getCommand());
            });
            selectTab(1);
            fireLabel("Save article", "Ctrl+S");
        });
    }

    /**
     * Test of {@link SideMenuController#makeArticle}.
     */
    @Test
    public void test_makeArticle() {
        Platform.runLater(() -> {
            subscribe(m -> {
                final ArticleMessage am = (ArticleMessage) m;
                assertEquals(ArticleMessage.Command.MAKE, am.getCommand());
            });
            selectTab(1);
            fireLabel("Make new article");
        });
    }

    /**
     * Test of {@link SideMenuController#callCopy}.
     */
    @Test
    public void test_callCopy() {
        Platform.runLater(() -> {
            subscribe(m -> {
                final ArticleMessage am = (ArticleMessage) m;
                assertEquals(ArticleMessage.Command.COPY, am.getCommand());
            });
            selectTab(1);
            fireLabel("Copy article");
        });
    }

    /**
     * Test of {@link SideMenuController#callRename}.
     */
    @Test
    public void test_callRename() {
        Platform.runLater(() -> {
            subscribe(m -> {
                final ArticleMessage am = (ArticleMessage) m;
                assertEquals(ArticleMessage.Command.RENAME, am.getCommand());
            });
            selectTab(1);
            fireLabel("Rename article");
        });
    }

    /**
     * Test of {@link SideMenuController#callDelete}.
     */
    @Test
    public void test_callDelete() {
        Platform.runLater(() -> {
            subscribe(m -> {
                final ArticleMessage am = (ArticleMessage) m;
                assertEquals(ArticleMessage.Command.DELETE, am.getCommand());
            });
            selectTab(1);
            fireLabel("Delete article");
        });
    }

    /**
     * Test of {@link SideMenuController#slideShow}.
     */
    @Test
    public void test_slideShow() {
        Platform.runLater(() -> {
            subscribe(m -> {
                final ArticleMessage am = (ArticleMessage) m;
                assertEquals(ArticleMessage.Command.SLIDE_SHOW, am.getCommand());
            });
            selectTab(0);
            fireLabel("Slide show", "Shift+F5");
        });
    }

    /**
     * Test of {@link SideMenuController#callEditor}.
     */
    @Test
    public void test_callEditor() {
        Platform.runLater(() -> {
            subscribe(m -> {
                final TabMessage am = (TabMessage) m;
                assertEquals(TabMessage.Command.EDIT, am.getCommand());
            });
            selectTab(1);
            fireLabel("Edit by editor", "Ctrl+E");
        });
    }

    /**
     * Test of {@link SideMenuController#callGC}.
     */
    @Test
    public void test_callGC() {
        Platform.runLater(() -> {
            subscribe(m -> {
                final SnackbarMessage am = (SnackbarMessage) m;
                assertEquals("Called garbage collection.", am.getText());
            });
            selectTab(3);
            fireLabel("Launch Garbage Collection");
        });
    }

    /**
     *
     * @param labelText
     * @param acceleratorText
     */
    private void fireLabel(final String labelText, final String acceleratorText) {
        fireLabel(labelText + "\t|\t" + acceleratorText);
    }

    /**
     * Lookup and fire event label.
     * @param labelText
     */
    private void fireLabel(final String labelText) {
        final Optional<Node> findFirst = currentList().getItems().stream()
                .filter(item -> (item instanceof MenuLabel))
                .filter(item -> labelText.equals(((Label) item).getText()))
                .findFirst();
        findFirst.ifPresent(item -> item.fireEvent(new ActionEvent()));
        if (findFirst.isPresent()) {
            return;
        }
        fail(labelText + " is not found." + currentList().getItems().toString());
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private ListView<Node> currentList() {
        return (ListView) tabPane.getSelectionModel().getSelectedItem().getContent();
    }

    /**
     * Select specified index' tab.
     * @param index tab index
     */
    private void selectTab(final int index) {
        tabPane.getSelectionModel().select(index);
    }

    /**
     * Subscribe passed consumer.
     * @param consumer
     */
    private void subscribe(final Consumer<? super Message> consumer) {
        messenger.subscribe(consumer, e -> fail(e.getMessage()));
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public void start(final Stage stage) throws Exception {
        this.stage = stage;
        final FXMLLoader loader
            = new FXMLLoader(getClass().getClassLoader().getResource("scenes/SideMenu.fxml"));
        try {
            final Pane root = loader.load();
            controller = (SideMenuController) loader.getController();
            stage.setScene(new Scene(root));
            controller.setStage(stage);
            controller.setConfig(makeConfig());
            controller.initialize(null, null);
        } catch (final IOException e) {
            e.printStackTrace();
            Platform.exit();
        }
        messenger = (TopicProcessor) Whitebox.getInternalState(controller, "messenger");
        stage.show();

        tabPane = (JFXTabPane) lookup("#menuTabs").query();
    }

    /**
     * Initialize {@link Config}.
     * @return
     * @throws URISyntaxException
     */
    private Config makeConfig() throws URISyntaxException {
        final Path path = Paths.get(getClass().getClassLoader()
                .getResource("conf/conf.properties").toURI());
        return new Config(path);
    }

}
