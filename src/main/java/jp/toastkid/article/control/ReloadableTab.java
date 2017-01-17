package jp.toastkid.article.control;

import java.util.Optional;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jfoenix.controls.JFXButton;

import javafx.beans.property.DoubleProperty;
import javafx.print.PrinterJob;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;

/**
 * Reloadable tab.
 *
 * @author Toast kid
 *
 */
public abstract class ReloadableTab extends Tab {

    /** Logger. */
    private static final Logger LOGGER = LoggerFactory.getLogger(ReloadableTab.class);

    /** Default loading tab title. */
    protected static final String LOADING = "Now Loading...";

    /** Default tab title. */
    protected static final String DEFAULT_TAB_TITLE = "New tab";

    /** Closing action. */
    private Consumer<Tab> closeAction;

    /**
     * Constructor.
     * @param title
     * @param content
     * @param closeAction
     */
    public ReloadableTab(final String title, final Node content, final Consumer<Tab> closeAction) {
        super(title, content);
        Optional.ofNullable(closeAction).ifPresent(action -> {
            final Button closeButton = new JFXButton("x");
            closeButton.setOnAction(e -> close(action));
            this.setGraphic(closeButton);
        });
    }

    /**
     * Close this tab.
     * @param action
     */
    public void close(final Consumer<Tab> action) {
        if (this.getText().startsWith("* ")) {
            // TODO snackbar 通知
            LOGGER.info("can't close");
            return;
        }
        action.accept(this);
    }

    /** Reload tab. */
    public abstract void reload();

    /** Load URL. */
    public abstract void loadUrl(final String url);

    /** Return this tab can load url. */
    public abstract boolean canLoadUrl();

    /** Print current page. */
    public abstract void print(final PrinterJob job);

    /** Get title. */
    public abstract String getTitle();

    /** Get URL. */
    public abstract String getUrl();

    /** Move to top. */
    public abstract void moveToTop();

    /** Move to bottom. */
    public abstract void moveToBottom();

    /** Stop loading. */
    public abstract void stop();

    /**
     * Highlight keyword.
     * @param word keyword which is wanted highlighting
     * @param script
     * @see <a href="http://aoe-tk.hatenablog.com/entry/2015/06/15/001217">
     * JavaFXのWebViewの検索を実現するのにもっと簡単な方法がありました</a>
     */
    public abstract void highlight(final Optional<String> word, final String script);

    /** Zoom property. */
    public abstract DoubleProperty zoomProperty();

    /**
     * Return closing action.
     * @return
     */
    protected Consumer<Tab> getCloseAction() {
        return closeAction;
    }

    /**
     * Set closing action.
     * @param closeAction
     */
    protected void setCloseAction(Consumer<Tab> closeAction) {
        this.closeAction = closeAction;
    }

    /**
     * Edit current content.
     * @return error message.
     */
    public String edit() {
        return "This tab's content can't edit.";
    }

    /**
     * Save current content.
     * @return error message.
     */
    public String saveContent() {
        return "This tab's content can't save.";
    }

    /**
     * Is this tab editing?.
     * @return
     */
    public boolean isEditing() {
        return false;
    }
}
