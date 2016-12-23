package jp.toastkid.article.models;

/**
 * ContentType.
 *
 * @author Toast kid
 */
public enum ContentType {

    HTML("text/html"), TEXT("text/plain");

    /** type. */
    private final String text;

    /**
     * Call from internal.
     * @param text
     */
    private ContentType(final String text) {
        this.text = text;
    }

    /**
     * Get text.
     * @return text/html, text/plain
     */
    public String getText() {
        return text;
    }
}
