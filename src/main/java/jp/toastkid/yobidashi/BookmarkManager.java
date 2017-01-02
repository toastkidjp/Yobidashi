package jp.toastkid.yobidashi;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Collectors;

import org.eclipse.collections.api.list.MutableList;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;

import javafx.stage.Window;
import jp.toastkid.dialog.AlertDialog;
import jp.toastkid.libs.utils.FileUtil;

/**
 * Bookmark manager.
 *
 * @author Toast kid
 */
public class BookmarkManager {

    /** path/to/bookmark. */
    private static final Path PATH = Paths.get("user/bookmark.txt");

    /** Bookmark content area. */
    private final CodeArea bookmarks;

    /**
     * Constructor.
     */
    public BookmarkManager() {
        bookmarks = new CodeArea();
        bookmarks.setParagraphGraphicFactory(LineNumberFactory.get(bookmarks));
    }

    /**
     * Edit bookmarks.
     *
     * @param parent This dialog's parent window
     */
    public void edit(final Window parent) {
        readBookmark();

        new AlertDialog.Builder(parent)
            .setTitle("Edit Bookmark")
            .setMessage("Would you input article titles.")
            .addControl(bookmarks)
            .build().show();
        store(bookmarks.getText());
    }

    /**
     * Store content to file.
     * @param newContent
     */
    private void store(final String newContent) {
        try {
            Files.write(PATH, newContent.getBytes(Defines.ARTICLE_ENCODE));
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Read bookmark list.
     */
    private void readBookmark() {
        if (!Files.exists(PATH)) {
            return;
        }
        try {
            final String currentContent
                = Files.readAllLines(PATH).stream().collect(Collectors.joining(System.lineSeparator()));
            bookmarks.replaceText(currentContent);
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Read content with MutableList.
     * @return
     */
    public MutableList<String> readLines() {
        return FileUtil.readLines(PATH.toFile(), Defines.ARTICLE_ENCODE);
    }

}
