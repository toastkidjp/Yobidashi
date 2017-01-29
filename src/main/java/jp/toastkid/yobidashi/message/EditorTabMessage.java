package jp.toastkid.yobidashi.message;

import java.nio.file.Path;

/**
 * Editor tab's message.
 *
 * @author Toast kid
 *
 */
public class EditorTabMessage implements Message {

    /** Path. */
    private final Path path;

    /**
     * Call from internal.
     * @param path
     */
    private EditorTabMessage(Path path) {
        this.path = path;
    }

    /**
     * Make new instance.
     * @param path
     * @return {@link EditorTabMessage}
     */
    public static EditorTabMessage make(Path path) {
        return new EditorTabMessage(path);
    }

    /**
     * Return this path.
     * @return Path
     */
    public Path getPath() {
        return path;
    }
}
