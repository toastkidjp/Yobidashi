package jp.toastkid.yobidashi.popup;

import static org.junit.Assert.assertEquals;

import java.util.HashSet;
import java.util.Set;

import org.junit.AfterClass;
import org.junit.Test;
import org.testfx.framework.junit.ApplicationTest;

import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Labeled;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import jp.toastkid.yobidashi.message.ApplicationMessage;

/**
 * {@link HamburgerPopup}'s test case.
 *
 * @author Toast kid
 *
 */
public class HamburgerPopupTest extends ApplicationTest {

    /** Counter. */
    private static Set<ApplicationMessage.Command> counter = new HashSet<>();

    /** Popup. */
    private HamburgerPopup popup;

    /** Button. */
    private Button button;

    private ListView<Labeled> listView;

    /**
     * Check of {@link HamburgerPopup} minimize.
     */
    @Test
    public void test_minimize() {
        find("Minimize").fireEvent(new ActionEvent());
    }

    /**
     * Check of {@link HamburgerPopup} quit.
     */
    @Test
    public void test_quit() {
        find("Quit").fireEvent(new ActionEvent());
    }

    /**
     * Find label from listView.
     * @param text label's text
     * @return Labeled object
     */
    private Labeled find(final String text) {
        for (final Labeled l : listView.getItems()) {
            if (text.equals(l.getText())) {
                return l;
            }
        }
        return null;
    }

    @Override
    public void start(final Stage stage) throws Exception {
        button = new Button();
        button.setOnAction(e -> popup.show());
        final HBox p = new HBox(button);
        stage.setScene(new Scene(p));
        popup = new HamburgerPopup.Builder()
                .setContainer(p)
                .setSource(button)
                .setConsumer(m -> counter.add(((ApplicationMessage) m).getCommand()))
                .build();
        stage.show();
        button.fire();
        listView = lookup("#popupList").query();
    }

    /**
     * Check set size.
     */
    @AfterClass
    public static void close() {
        assertEquals(2, counter.size());
    }

}
