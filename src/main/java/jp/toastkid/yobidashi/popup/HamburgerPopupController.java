package jp.toastkid.yobidashi.popup;

import com.jfoenix.controls.JFXPopup;

import javafx.fxml.FXML;
import jp.toastkid.yobidashi.message.ApplicationMessage;
import jp.toastkid.yobidashi.message.Message;
import reactor.core.publisher.TopicProcessor;

/**
 * Hamburger popup's controller.
 *
 * @author Toast kid
 *
 */
public class HamburgerPopupController {

    /** Popup. */
    @FXML
    private JFXPopup popup;

    /** Message sender. */
    private final TopicProcessor<Message> messenger;

    /**
     * Initialize messenger.
     */
    public HamburgerPopupController() {
        messenger = TopicProcessor.create();
    }

    /**
     * Minimize window.
     */
    @FXML
    private void minimize() {
        getMessenger().onNext(ApplicationMessage.makeMinimize());
    }

    /**
     * Quit this app.
     */
    @FXML
    private void quit() {
        getMessenger().onNext(ApplicationMessage.makeQuit());
    }

    /**
     * Return this controller's messenger.
     * @return messenger
     */
    TopicProcessor<Message> getMessenger() {
        return messenger;
    }

    /**
     * Return {@link JFXPopup} object.
     * @return popup
     */
    JFXPopup getPopup() {
        return popup;
    }

}
