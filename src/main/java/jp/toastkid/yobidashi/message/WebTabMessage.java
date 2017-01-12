package jp.toastkid.yobidashi.message;

import jp.toastkid.article.control.WebTab;
import jp.toastkid.article.models.ContentType;

/**
 * {@link WebTab}'s message.
 *
 * @author Toast kid
 *
 */
public class WebTabMessage implements Message {

    /** Target tab's title. */
    private final String title;

    /** Target tab's content HTML. */
    private final String content;

    /** Target tab's content-type. */
    private final ContentType contentType;

    /**
     * Call from only internal.
     * @param t
     * @param c
     * @param type
     */
    private WebTabMessage(final String t, final String c, final ContentType type) {
        this.title   = t;
        this.content = c;
        this.contentType = type;
    }

    /**
     * Make instance with title, content, and content-type.
     * @param t
     * @param c
     * @param type
     * @return instance
     */
    public static WebTabMessage make(final String t, final String c, final ContentType type) {
        return new WebTabMessage(t, c, type);
    }

    /**
     * Getter of title.
     * @return
     */
    public String getTitle() {
        return title;
    }

    /**
     * Getter of content.
     * @return
     */
    public String getContent() {
        return content;
    }

    /**
     * Getter of content-type.
     * @return
     */
    public ContentType getContentType() {
        return contentType;
    }

}
