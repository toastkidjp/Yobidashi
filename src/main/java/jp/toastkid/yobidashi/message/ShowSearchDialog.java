package jp.toastkid.yobidashi.message;

/**
 * Show search dialog.
 *
 * @author Toast kid
 *
 */
public class ShowSearchDialog implements Message {

    /**
     * Call only internal.
     */
    private ShowSearchDialog() {
        // NOP.
    }

    /**
     * Make empty instance.
     * @return {@link ShowSearchDialog}
     */
    public static ShowSearchDialog make() {
        return new ShowSearchDialog();
    }

}
