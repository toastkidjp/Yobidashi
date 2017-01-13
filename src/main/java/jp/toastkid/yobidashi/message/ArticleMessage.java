package jp.toastkid.yobidashi.message;

/**
 * Article action's message.
 *
 * @author Toast kid
 *
 */
public class ArticleMessage implements Message {

    /**
     * Command.
     * @author Toast kid
     *
     */
    public enum Command {
        MAKE, LENGTH, COPY, RENAME, SLIDE_SHOW, DELETE,
        CONVERT_AOBUN, CONVERT_EPUB, WORD_CLOUD
    }

    /** This instance's command. */
    private final Command command;

    /**
     * Internal constructor.
     * @param c
     */
    private ArticleMessage(final Command c) {
        this.command = c;
    }

    /**
     * Return command.
     * @return Command.
     */
    public Command getCommand() {
        return command;
    }

    /**
     * Make length message.
     * @return LENGTH message
     */
    public static ArticleMessage makeLength() {
        return new ArticleMessage(Command.LENGTH);
    }

    /**
     * Make slide show message.
     * @return SLIDE_SHOW message
     */
    public static ArticleMessage makeSlideShow() {
        return new ArticleMessage(Command.SLIDE_SHOW);
    }

    /**
     * Make make-new message.
     * @return MAKE message
     */
    public static ArticleMessage makeNew() {
        return new ArticleMessage(Command.MAKE);
    }

    /**
     * Make copy message.
     * @return COPY message
     */
    public static ArticleMessage makeCopy() {
        return new ArticleMessage(Command.COPY);
    }

    /**
     * Make rename message.
     * @return RENAME message
     */
    public static ArticleMessage makeRename() {
        return new ArticleMessage(Command.RENAME);
    }

    /**
     * Make delete message.
     * @return DELETE message
     */
    public static ArticleMessage makeDelete() {
        return new ArticleMessage(Command.DELETE);
    }

    /**
     * Make convert aobun message.
     * @return CONVERT_AOBUN message
     */
    public static ArticleMessage makeConvertAobun() {
        return new ArticleMessage(Command.CONVERT_AOBUN);
    }

    /**
     * Make convert ePub message.
     * @return CONVERT_EPUB message
     */
    public static ArticleMessage makeConvertEpub() {
        return new ArticleMessage(Command.CONVERT_EPUB);
    }

    /**
     * Make word cloud message.
     * @return WORD_CLOUD message
     */
    public static ArticleMessage makeWordCloud() {
        return new ArticleMessage(Command.WORD_CLOUD);
    }

}
