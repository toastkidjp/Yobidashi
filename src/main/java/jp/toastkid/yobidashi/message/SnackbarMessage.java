package jp.toastkid.yobidashi.message;

/**
 * TODO write test.
 * @author Toast kid
 *
 */
public class SnackbarMessage implements Message {

    public enum Command {
        SHOW
    }

    private final Command command;
    private final String text;

    private SnackbarMessage(final Command c, final String text) {
        this.command = c;
        this.text = text;
    }

    public static SnackbarMessage makeShow(final String text) {
        return new SnackbarMessage(Command.SHOW, text);
    }

    public Command getCommand() {
        return command;
    }

    public String getText() {
        return text;
    }
}
