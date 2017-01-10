package jp.toastkid.yobidashi.message;

import javafx.scene.layout.Pane;

/**
 * TODO write test.
 * @author Toast kid
 *
 */
public class ContentTabMessage implements Message {

    private final String title;

    private final Pane   content;

    private ContentTabMessage(final String t, final Pane c) {
        this.title   = t;
        this.content = c;
    }

    public static ContentTabMessage make(final String t, final Pane c) {
        return new ContentTabMessage(t, c);
    }

    public String getTitle() {
        return title;
    }

    public Pane getContent() {
        return content;
    }

}
