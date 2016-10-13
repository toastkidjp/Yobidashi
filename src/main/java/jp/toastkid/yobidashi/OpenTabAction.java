package jp.toastkid.yobidashi;

import javafx.scene.layout.Pane;

/**
 * Action of opening tab.
 *
 * @author Toast kid
 *
 */
interface OpenTabAction {
    void draw(final String title, final Pane content);
}
