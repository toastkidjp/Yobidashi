package jp.toastkid.gui.jfx.wiki.models;

import java.io.File;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.eclipse.collections.api.set.FixedSizeSet;
import org.eclipse.collections.impl.factory.Sets;

import jp.toastkid.gui.jfx.wiki.Functions;
import jp.toastkid.libs.utils.FileUtil;
import jp.toastkid.libs.utils.Strings;

/**
 * Article model.
 * @author Toast kid
 *
 */
public class Article implements Comparable<Article> {

    /** article file. */
    public File file;

    /** article's extension. */
    private String extension;

    /** article title. */
    public String title;

    /** number of article characters. */
    public long byteLength;

    /** current yOffset. */
    public int  yOffset = 0;

    /**
     * Article's extension.
     * @author Toast kid
     *
     */
    public static enum Extension {
        MD(".md"), WIKI(".txt"), SLIDE(".slide");

        private String text;
        private Extension(final String n) {
            this.text = n;
        }

        public String text() {
            return text;
        }
    }

    /** usable extensions. */
    private static final FixedSizeSet<String> EXTENSIONS
    = Sets.fixedSize.of(Extension.SLIDE.text, Extension.WIKI.text, Extension.MD.text);

    /**
     * initialize Article model.
     * @param file
     */
    public Article(final File file) {
        if (file == null) {
            throw new IllegalArgumentException("file is not allow null.");
        }

        this.file      = file;
        this.title     = convertTitle(file.getName());
        this.extension = FileUtil.findExtension(file).get();
        //this.byteLength = (file.length() / 1024L);
        //FileUtil.countCharacters(this.file.getAbsolutePath(), Defines.ARTICLE_ENCODE);
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
        return String.format("/%s/%s%s", this.extention().substring(1),
                Functions.toBytedString_EUC_JP(this.title), this.extention());
    }

    /**
     * replace file.
     * @param dest new file.
     */
    public void replace(final File dest) {
        this.file      = dest;
        this.title     = Article.convertTitle(dest.getName());
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
        return Strings.toUniTypeDate(file.lastModified());
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
     * ファイル名(<b>path は不可</b>)を人間の読める形式にして返す.
     * 「.txt」 が入っていても可
     * <HR>
     * (121014) 作成<BR>
     * @param filePath
     * @return ファイル名
     */
    public static final String convertTitle(final String filePath) {
        return (StringUtils.isNotEmpty(filePath))
                                        ? Functions.decodeBytedStr(FileUtil.removeExtension(filePath), "EUC-JP" )
                                                                        : null;
    }

    /**
     * fileName から Article オブジェクトを生成.
     * @param fileName ファイル名(パスではない)
     * @return Article オブジェクト
     */
    public static Article find(final String fileName) {
        return new Article(new File(Config.get("articleDir"), fileName));
    }

    /**
     * distinguish usable file extension.
     * @param f File Object
     * @return f is valid file?
     */
    public static boolean isValidContentFile(final File f) {
        final Optional<String> ext = FileUtil.findExtension(f);
        if (!ext.isPresent()) {
            return false;
        }
        return EXTENSIONS.contains(ext.get());
    }


}
