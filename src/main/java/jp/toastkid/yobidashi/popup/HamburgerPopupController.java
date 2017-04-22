package jp.toastkid.yobidashi.popup;

import com.jfoenix.controls.JFXPopup;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;
import javafx.fxml.FXML;
import jp.toastkid.yobidashi.message.ApplicationMessage;
import jp.toastkid.yobidashi.message.Message;

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
    private final Subject<Message> messenger;

    /**
     * Initialize messenger.
     */
    public HamburgerPopupController() {
        messenger = PublishSubject.create();
    }

    /**
     * Minimize window.
     */
    @FXML
    private void minimize() {
    	messenger.onNext(ApplicationMessage.makeMinimize());
    }

    /**
     * Quit this app.
     */
    @FXML
    private void quit() {
        messenger.onNext(ApplicationMessage.makeQuit());
    }

    /**
     * Return this controller's subscription.
     * @return messenger's subscription
     */
    Disposable subscribe(final Consumer<Message> c) {
        return messenger.subscribe(c);
    }

    /**
     * Return {@link JFXPopup} object.
     * @return popup
     */
    JFXPopup getPopup() {
        return popup;
    }

}
