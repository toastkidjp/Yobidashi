package jp.toastkid.libs.lambda;

/**
 * Common predicates.
 *
 * @author Toast kid
 */
public class Filters {

    /**
     * Deny make instance.
     */
    private Filters() {
        // NOP.
    }

    /**
     * Return Predicate use filter out null.
     * @return filter out null objects.
     */
    public static <T> boolean isNotNull(final T t) {
        return t != null;
    }

    /**
     * Return Predicate use filter out not-null.
     * @return filter out not-null objects.
     */
    public static <T> boolean isNull(final T t) {
        return t == null;
    }
}
