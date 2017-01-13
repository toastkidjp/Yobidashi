package jp.toastkid.yobidashi.message;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;

/**
 * {@link ShowSearchDialog}'s test case.
 *
 * @author Toast kid
 *
 */
public class ShowSearchDialogTest {

    /**
     * Check {@link ShowSearchDialog#make()}.
     */
    @Test
    public void testMake() {
        final ShowSearchDialog make = ShowSearchDialog.make();
        assertNotNull(make);
    }

}
