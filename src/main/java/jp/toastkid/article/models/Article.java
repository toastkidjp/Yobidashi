package jp.toastkid.article.models;

import java.io.File;
import java.util.Optional;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.eclipse.collections.api.set.FixedSizeSet;
import org.eclipse.collections.impl.factory.Sets;

import jp.toastkid.libs.utils.CalendarUtil;
import jp.toastkid.libs.utils.FileUtil;
import jp.toastkid.yobidashi.Defines;

/**
 * Article model.
 *
 * @author Toast kid
 */
public class Article implements Comparable<Article> {

    /** Internal link's protocol. */
    public static final String INTERNAL_PROTOCOL = "file:///internal/";

    /** Internal link's format. */
    private static final String INTERNAL_LINK_FORMAT = INTERNAL_PROTOCOL + "/%s/%s%s";

    /** article file. */
    public File file;

    /** article's extension. */
    private String extension;

    /** article title. */
    public String title;

    /** number of article characters. */
    public long byteLength;

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
     * @param file
     */
    public Article(final File file) {
        if (file == null) {
            throw new IllegalArgumentException("file is not allow null.");
        }

        this.file      = file;
        this.title     = Articles.convertTitle(file.getName());
        this.extension = FileUtil.findExtension(file).orElse("");
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
     * replace file.
     * @param dest new file.
     */
    public void replace(final File dest) {
        this.file      = dest;
        this.title     = Articles.convertTitle(dest.getName());
        this.extension = FileUtil.findExtension(file).get();
    }

    /**
     * return last modified ms.
     * @return last modified ms.
     */
    public long lastModified() {
        return file.lastModified();
    }

    /**
     * return last modified ms.
     * @return last modified string.
     */
    public String lastModifiedText() {
        return CalendarUtil.toUniTypeDate(file.lastModified());
    }

    @Override
    public boolean equals(final Object obj) {
        if (!(obj instanceof Article)) {
            return false;
        }
        return this.file.equals(((Article) obj).file);
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
        return new Article(this.file);
    }

    /**
     * distinguish usable file extension.
     * @param f File Object
     * @return f is valid file?
     */
    public boolean isValid() {
        final Optional<String> ext = FileUtil.findExtension(this.file);
        if (!ext.isPresent()) {
            return false;
        }
        return EXTENSIONS.contains(ext.get());
    }

    /**
     * ファイルの字数計測結果を文字列にまとめて返す.
     * @return ファイルの字数計測結果(文字列)
     */
    public final String makeCharCountResult() {
        return new StringBuilder()
            .append(title).append(" は ")
            .append(FileUtil.countCharacters(file.getAbsolutePath(), Defines.ARTICLE_ENCODE))
            .append(" 字です。").append(System.lineSeparator())
            .append(file.length() / 1024L).append("[KB]").toString();
    }

}
