package jp.toastkid.speed_dial;

import java.util.Optional;
import java.util.function.BiConsumer;

import org.apache.commons.lang3.StringUtils;
import org.reactfx.util.TriConsumer;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;

/**
 * Speed Dial's controller.
 *
 * @author Toast kid
 */
public class Controller {

    /** Root pane. */
    @FXML
    private Pane root;

    /** title. */
    @FXML
    private Label title;

    /** Input field. */
    @FXML
    private TextField input;

    /** Search type. */
    @FXML
    private ComboBox<String> type;

    /** Search action. */
    private BiConsumer<String, String> webSearchAction;

    /** Empty action. */
    private Runnable emptyAction;

    /** Action of search article. */
    private TriConsumer<Boolean, String, String> articleSearchAction;

    /**
     * Return root pane.
     * @return root pane.
     */
    public Pane getRoot() {
        return root;
    }

    /**
     * Set passed text to title.
     * @param title title text
     */
    public void setTitle(final String title) {
        if (StringUtils.isBlank(title)) {
            return;
        }
        this.title.setText(title);
    }

    /**
     * Set search action.
     * @param searchAction
     */
    public void setOnWebSearch(final BiConsumer<String, String> searchAction) {
        this.webSearchAction = searchAction;
    }

    /**
     * Set type's zero.
     */
    public void setZero() {
        this.type.getSelectionModel().select(0);
    }

    /**
     * Do search.
     */
    @FXML
    private void search() {
        final String query = input.getText();
        final String t = type.getSelectionModel().getSelectedItem();
        if (StringUtils.isBlank(query) || StringUtils.isBlank(t)) {
            emptyAction.run();
            return;
        }
        if ("Article".equals(t)) {
            articleSearchAction.accept(true, query, "");
            return;
        }
        webSearchAction.accept(query, t);
    }

    /**
     * Set background image to root pane's background.
     * @param background
     */
    public void setBackground(final String background) {
        Optional.ofNullable(background).ifPresent(this::applyBackgroundStyle);
    }

    /**
     * Apply background style.
     * @param imageUrl
     */
    private void applyBackgroundStyle(final String imageUrl) {
        root.setStyle("-fx-background-image: url('" + imageUrl + "'); "
                + "-fx-background-position: center center; "
                + "-fx-background-size: cover;"
                + "-fx-background-repeat: stretch;");
    }

    /**
     * Set on empty action.
     * @param emptyAction
     */
    public void setOnEmptyAction(final Runnable emptyAction) {
        this.emptyAction = emptyAction;
    }

    /**
     * Set on article search action.
     * @param command
     */
    public void setOnArticleSearch(TriConsumer<Boolean, String, String> command) {
        this.articleSearchAction = command;
    }

}
