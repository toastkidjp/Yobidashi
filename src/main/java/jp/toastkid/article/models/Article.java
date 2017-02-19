package jp.toastkid.article.models;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.eclipse.collections.api.set.FixedSizeSet;
import org.eclipse.collections.impl.factory.Sets;

import javafx.scene.control.ListView;
import jp.toastkid.libs.utils.CalendarUtil;
import jp.toastkid.libs.utils.FileUtil;

/**
 * Article model.
 *
 * @author Toast kid
 */
public class Article implements Comparable<Article> {

    /** Article file's encoding. */
    public static final String ENCODE            = "UTF-8";

    /** Internal link's protocol. */
    public static final String INTERNAL_PROTOCOL = "file:///internal/";

    /** 左のリストで中心をいくつずらすか. */
    private static final int FOCUS_MARGIN = 10;

    /** Internal link's format. */
    private static final String INTERNAL_LINK_FORMAT = INTERNAL_PROTOCOL + "/%s/%s%s";

    /** article file. */
    public Path path;

    /** article's extension. */
    private String extension;

    /** article title. */
    public String title;

    /**
     * Article's extension.
     * @author Toast kid
     *
     */
    public static enum Extension {
        MD(".md");

        private String text;

        private Extension(final String n) {
            this.text = n;
        }

        public String text() {
            return text;
        }
    }

    /** usable extensions. */
    public static final FixedSizeSet<String> EXTENSIONS = Sets.fixedSize.of(Extension.MD.text);

    /**
     * initialize Article model.
     * @param path
     */
    public Article(final Path path) {
        if (path == null) {
            throw new IllegalArgumentException("file is not allow null.");
        }

        this.path      = path;
        this.title     = Articles.convertTitle(path.getFileName().toString());
        this.extension = FileUtil.findExtension(path).orElse("");
    }

    /**
     * get file's extension.
     * @return
     */
    public String extention() {
        return this.extension;
    }

    /**
     * 記事ファイルへの内部的なパスを取得する.
     * <HR>
     * (130512) メソッドに抽出<BR>
     * @param selectedDocTitle 平文の記事名
     * @return 記事ファイルへのパス
     */
    public String toInternalUrl() {
        return String.format(INTERNAL_LINK_FORMAT, this.extention().substring(1),
                Articles.titleToFileName(this.title), this.extention());
    }

    /**
     * Replace file.
     * @param dest new file.
     */
    public void replace(final Path dest) {
        this.path      = dest;
        this.title     = Articles.convertTitle(dest);
        this.extension = FileUtil.findExtension(path).get();
    }

    /**
     * return last modified ms.
     * @return last modified ms.
     */
    public long lastModified() {
        try {
            return Files.getLastModifiedTime(path).toMillis();
        } catch (final IOException e) {
            e.printStackTrace();
        }
        return -1L;
    }

    /**
     * return last modified ms.
     * @return last modified string.
     */
    public String lastModifiedText() {
        try {
            return CalendarUtil.toUniTypeDate(Files.getLastModifiedTime(path).toMillis());
        } catch (final IOException e) {
            e.printStackTrace();
        }
        return "-1L";
    }

    @Override
    public boolean equals(final Object obj) {
        if (!(obj instanceof Article)) {
            return false;
        }
        return this.path.equals(((Article) obj).path);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this, true);
    }

    @Override
    public int compareTo(final Article o) {
        return this.title.compareTo(o.title);
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    @Override
    public Article clone() {
        return new Article(this.path);
    }

    /**
     * distinguish usable file extension.
     * @param f File Object
     * @return f is valid file?
     */
    public boolean isValid() {
        final Optional<String> ext = FileUtil.findExtension(this.path);
        if (!ext.isPresent()) {
            return false;
        }
        return EXTENSIONS.contains(ext.get());
    }

    /**
     * Focus ListView on this item.
     * @param listView
     */
    public final void focus(final ListView<Article> listView) {
        final int indexOf = listView.getItems().indexOf(this);
        if (indexOf != -1){
            listView.getSelectionModel().select(indexOf);
            listView.scrollTo(indexOf - FOCUS_MARGIN);
            return;
        }
        listView.getSelectionModel().clearSelection();
    }

}
