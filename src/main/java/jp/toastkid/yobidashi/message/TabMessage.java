package jp.toastkid.yobidashi.message;

/**
 * Tab actions's message.
 *
 * @author Toast kid
 *
 */
public class TabMessage implements Message {

    /**
     * Command.
     * @author Toast kid
     *
     */
    public enum Command {
        OPEN, SAVE, EDIT, RELOAD, CLOSE, CLOSE_ALL, PREVIEW
    }

    /** This instance's command. */
    private final Command command;

    /**
     *
     * @param c
     */
    private TabMessage(final Command c) {
        this.command = c;
    }

    /**
     * Open.
     * @return
     */
    public static TabMessage makeOpen() {
        return new TabMessage(Command.OPEN);
    }

    /**
     * Preview.
     * @return
     */
    public static TabMessage makePreview() {
        return new TabMessage(Command.PREVIEW);
    }

    /**
     * Edit.
     * @return
     */
    public static TabMessage makeEdit() {
        return new TabMessage(Command.EDIT);
    }

    /**
     * Save.
     * @return
     */
    public static TabMessage makeSave() {
        return new TabMessage(Command.SAVE);
    }

    /**
     * Reload.
     * @return
     */
    public static TabMessage makeReload() {
        return new TabMessage(Command.RELOAD);
    }

    /**
     * Close.
     * @return
     */
    public static TabMessage makeClose() {
        return new TabMessage(Command.CLOSE);
    }

    /**
     * Close all.
     * @return
     */
    public static TabMessage makeCloseAll() {
        return new TabMessage(Command.CLOSE_ALL);
    }

    /**
     * Getter of Command.
     * @return
     */
    public Command getCommand() {
        return command;
    }

}
