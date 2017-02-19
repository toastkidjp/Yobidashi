package jp.toastkid.yobidashi.message;

import jp.toastkid.libs.WebServiceHelper;
import jp.toastkid.libs.WebServiceHelper.Type;

/**
 * Web search event's message.
 *
 * @author Toast kid
 *
 */
public class WebSearchMessage implements Message {

    /** search query. */
    private final String query;

    /** search type. */
    private final Type type;

    /**
     * Call from internal.
     * @param query
     * @param type
     */
    private WebSearchMessage(String query, String type) {
        this.query = query;
        this.type  = WebServiceHelper.Type.find(type);
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

    public Type type() {
        return type;
    }
}
