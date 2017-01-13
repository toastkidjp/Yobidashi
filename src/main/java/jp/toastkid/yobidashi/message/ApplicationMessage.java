package jp.toastkid.yobidashi.message;

/**
 * Application action.
 *
 * @author Toast kid
 *
 */
public class ApplicationMessage implements Message {

    /** Command. */
    public enum Command {
        QUIT
    }

    /** This message's command. */
    private final Command command;

    /**
     * Call from internal.
     * @param c
     */
    private ApplicationMessage(final Command c) {
        this.command = c;
    }

    /**
     * Return this object's command.
     * @return
     */
    public Command getCommand() {
        return command;
    }

    /**
     * Make QUIT message.
     * @return message object
     */
    public static ApplicationMessage makeQuit() {
        return new ApplicationMessage(Command.QUIT);
    }
}
