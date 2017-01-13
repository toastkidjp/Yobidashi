package jp.toastkid.yobidashi.message;

/**
 * Snackbar event.
 * @author Toast kid
 *
 */
public class SnackbarMessage implements Message {

    /**
     * Command.
     * @author Toast kid
     *
     */
    public enum Command {
        SHOW
    }

    /** This instance's command. */
    private final Command command;

    /** This instance's text. */
    private final String text;

    /**
     * Call from internal.
     * @param c
     * @param text
     */
    private SnackbarMessage(final Command c, final String text) {
        this.command = c;
        this.text = text;
    }

    /**
     * Make SHOW event.
     * @param text
     * @return
     */
    public static SnackbarMessage makeShow(final String text) {
        return new SnackbarMessage(Command.SHOW, text);
    }

    /**
     * Return Command.
     * @return
     */
    public Command getCommand() {
        return command;
    }

    /**
     * Return text.
     * @return
     */
    public String getText() {
        return text;
    }
}
