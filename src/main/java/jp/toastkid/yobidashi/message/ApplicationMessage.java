package jp.toastkid.yobidashi.message;

/**
 * TODO write test.
 * @author Toast kid
 *
 */
public class ApplicationMessage implements Message {

    public enum Command {
        QUIT
    }

    private final Command command;

    private ApplicationMessage(final Command c) {
        this.command = c;
    }

    public Command getCommand() {
        return command;
    }

    public static ApplicationMessage makeQuit() {
        return new ApplicationMessage(Command.QUIT);
    }
}
