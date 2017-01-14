package jp.toastkid.yobidashi.message;

/**
 * TODO write test.
 * Web search event's message.
 *
 * @author Toast kid
 *
 */
public class WebSearchMessage implements Message {

    /** search query. */
    private final String query;

    /** search type. */
    private final String type;

    /**
     * Call from internal.
     * @param query
     * @param type
     */
    private WebSearchMessage(String query, String type) {
        this.query = query;
        this.type  = type;
    }

    /**
     * Make with query and title.
     * @param query
     * @param type
     */
    public static WebSearchMessage make(String query, String type) {
        return new WebSearchMessage(query, type);
    }

    public String query() {
        return query;
    }

    public String type() {
        return type;
    }
}
