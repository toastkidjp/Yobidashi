package jp.toastkid.yobidashi.message;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import javafx.scene.layout.Pane;

/**
 * {@link ContentTabMessage}'s test case.
 *
 * @author Toast kid
 *
 */
public class ContentTabMessageTest {

    /**
     * Check {@link ContentTabMessage#make(String, Pane)}.
     */
    @Test
    public void test_make() {
        final String title = "title";
        final Pane   pane  = new Pane();
        final ContentTabMessage message = ContentTabMessage.make(title , pane );
        assertEquals(title, message.getTitle());
        assertEquals(pane,  message.getContent());
    }

}
