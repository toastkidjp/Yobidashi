package jp.toastkid.article.control;

import javafx.scene.control.Skin;
import javafx.scene.control.TabPane;
import javafx.scene.input.MouseEvent;

/**
 *
 * @author Toast kid
 * @deprecated
 */
@Deprecated
public class MaterialTabPane extends TabPane {

    private static final String DEFAULT_STYLE_CLASS = "jfx-tab-pane";

    public MaterialTabPane() {
        super();
        initialize();
    }

    @Override
    protected Skin<?> createDefaultSkin() {
        return new MaterialTabPaneSkin(this);
    }

    private void initialize() {
        this.getStyleClass().setAll(DEFAULT_STYLE_CLASS);
    }

    public void propagateMouseEventsToParent(){
        this.addEventHandler(MouseEvent.ANY, (e)->{
            e.consume();
            this.getParent().fireEvent(e);
        });
    }
}
