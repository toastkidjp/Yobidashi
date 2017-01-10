package jp.toastkid.yobidashi.message;

import jp.toastkid.article.models.ContentType;

/**
 * TODO write test.
 *
 * @author Toast kid
 *
 */
public class WebTabMessage implements Message {

    private final String title;

    private final String content;

    private final ContentType contentType;

    private WebTabMessage(final String t, final String c, final ContentType type) {
        this.title   = t;
        this.content = c;
        this.contentType = type;
    }

    public static WebTabMessage make(final String t, final String c, final ContentType type) {
        return new WebTabMessage(t, c, type);
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public ContentType getContentType() {
        return contentType;
    }

}
