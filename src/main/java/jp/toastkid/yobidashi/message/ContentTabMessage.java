package jp.toastkid.yobidashi.message;

import javafx.scene.layout.Pane;
import jp.toastkid.article.control.ContentTab;

/**
 * {@link ContentTab}'s action.
 *
 * @author Toast kid
 *
 */
public class ContentTabMessage implements Message {

    /** Target tab's title. */
    private final String title;

    /** Target tab's content. */
    private final Pane   content;

    /** After process. */
    private final Runnable doAfter;

    /**
     * This constructor is called from internal.
     * @param t title
     * @param c content pane
     */
    private ContentTabMessage(final String t, final Pane c, final Runnable doAfter) {
        this.title   = t;
        this.content = c;
        this.doAfter = doAfter;
    }

    /**
     * Make instance with title and content pane.
     * @param t
     * @param c
     * @return {@link ContentTabMessage} object
     */
    public static ContentTabMessage make(final String t, final Pane c) {
        return new ContentTabMessage(t, c, null);
    }

    /**
     * Make instance with title and content pane.
     * @param t
     * @param c
     * @param doAfter
     * @return {@link ContentTabMessage} object
     */
    public static ContentTabMessage make(final String t, final Pane c, final Runnable doAfter) {
        return new ContentTabMessage(t, c, doAfter);
    }

    /**
     * Return title.
     * @return title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Return content pane.
     * @return pane
     */
    public Pane getContent() {
        return content;
    }

    /**
     * Run after process.
     */
    public void doAfter() {
        if (doAfter == null) {
            return;
        }
        doAfter.run();
    }

}
