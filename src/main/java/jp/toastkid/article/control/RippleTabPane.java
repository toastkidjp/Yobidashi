package jp.toastkid.article.control;

import com.jfoenix.controls.JFXTabPane;

import javafx.scene.control.Skin;
import javafx.scene.control.TabPane;

/**
 * Ripple {@link TabPane}'s skin.
 *
 * @author Toast kid
 *
 */
public class RippleTabPane extends JFXTabPane {

    @Override
    protected Skin<?> createDefaultSkin() {
        return new RippleTabSkin(this);
    }
}
