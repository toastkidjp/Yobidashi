package jp.toastkid.yobidashi.message;

/**
 * Search action's message.
 *
 * @author Toast kid
 *
 */
public class ArticleSearchMessage implements Message {

    /** Query. */
    private final String query;

    /** Filter. */
    private final String filter;

    /**
     * Internal constructor.
     * @param q
     * @param f
     */
    private ArticleSearchMessage(String q, String f) {
        this.query = q;
        this.filter = f;
    }

    /**
     * Make with query and empty filter string.
     * @param query search query
     * @return {@link ArticleSearchMessage}
     */
    public static ArticleSearchMessage make(final String query) {
        return new ArticleSearchMessage(query, "");
    }

    /**
     * Make with query and filter string.
     * @param query search query
     * @param filter search filter string
     * @return {@link ArticleSearchMessage}
     */
    public static ArticleSearchMessage make(final String query, final String filter) {
        return new ArticleSearchMessage(query, filter);
    }

    /**
     * Getter of query.
     * @return query
     */
    public String query() {
        return query;
    }

    /**
     * Getter of filter.
     * @return filter
     */
    public String filter() {
        return filter;
    }

}
