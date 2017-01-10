package jp.toastkid.yobidashi.message;

/**
 * TODO write test.
 * Article action's message.
 *
 * @author Toast kid
 *
 */
public class ArticleMessage implements Message {

    public enum Command {
        MAKE, LENGTH, COPY, RENAME, SEARCH, SLIDE_SHOW, DELETE,
        CONVERT_AOBUN, CONVERT_EPUB, WORD_CLOUD
    }

    private final Command command;

    private ArticleMessage(final Command c) {
        this.command = c;
    }

    public Command getCommand() {
        return command;
    }

    public static ArticleMessage makeLength() {
        return new ArticleMessage(Command.LENGTH);
    }

    public static ArticleMessage makeSlideShow() {
        return new ArticleMessage(Command.SLIDE_SHOW);
    }

    public static ArticleMessage makeSearch() {
        return new ArticleMessage(Command.SEARCH);
    }

    public static Message makeNew() {
        return new ArticleMessage(Command.MAKE);
    }

    public static ArticleMessage makeCopy() {
        return new ArticleMessage(Command.COPY);
    }

    public static ArticleMessage makeRename() {
        return new ArticleMessage(Command.RENAME);
    }

    public static ArticleMessage makeDelete() {
        return new ArticleMessage(Command.DELETE);
    }

    public static ArticleMessage makeConvertAobun() {
        return new ArticleMessage(Command.CONVERT_AOBUN);
    }

    public static ArticleMessage makeConvertEpub() {
        return new ArticleMessage(Command.CONVERT_EPUB);
    }

    public static ArticleMessage makeWordCloud() {
        return new ArticleMessage(Command.WORD_CLOUD);
    }

}
