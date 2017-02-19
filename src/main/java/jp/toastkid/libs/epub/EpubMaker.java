package jp.toastkid.libs.epub;

import java.io.BufferedInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipOutputStream;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.collections.api.list.ImmutableList;
import org.eclipse.collections.api.tuple.Pair;
import org.eclipse.collections.impl.factory.Lists;
import org.eclipse.collections.impl.factory.Maps;
import org.eclipse.collections.impl.tuple.Tuples;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jp.toastkid.article.models.Articles;
import jp.toastkid.libs.utils.CalendarUtil;
import jp.toastkid.libs.utils.FileUtil;
import jp.toastkid.libs.utils.Strings;

/**
 * ePub を生成する.
 *
 * @author Toast kid
 */
public final class EpubMaker {

    /** Logger. */
    private static final Logger LOGGER = LoggerFactory.getLogger(EpubMaker.class);

    /** 改行記号. */
    private static final String LINE_SEPARATOR = System.lineSeparator();

    /** コンテンツの格納エントリ. */
    private static final String CONTENT_DIR = "OEBPS/";

    /** メタデータの格納エントリ. */
    private static final String META_DIR    = "META-INF/";

    /** epub に同梱するコンテンツ(HTML or XHTML). */
    private List<ContentMetaData> contentPaths;

    /** メタデータ. */
    private final EpubMetaData meta;

    /** 必ず削除対象とするファイル. */
    private static final List<String> DEFAULT_REMOVE_TARGETS
        = Arrays.asList("content.opf", "navdoc.html", "title_page.xhtml", "toc.ncx");

    /** epub に必ず含めるメタファイル名とその参照元の組一覧. */
    private static final ImmutableList<Pair<String, String>> REQUIRED_METAFILE_PAIRS
        = Lists.immutable.with(
                Tuples.pair(Resource.MIMETYPE, ""),
                Tuples.pair(Resource.CONTAINER_XML, META_DIR),
                Tuples.pair(Resource.STYLESHEET, CONTENT_DIR),
                Tuples.pair(Resource.STYLESHEET_VERTICAL, CONTENT_DIR),
                Tuples.pair("navdoc.html", CONTENT_DIR),
                Tuples.pair("toc.ncx", CONTENT_DIR),
                Tuples.pair("content.opf", CONTENT_DIR),
                Tuples.pair("title_page.xhtml", CONTENT_DIR)
            );

    /**
     * 与えられたメタデータで初期化する.
     * @param meta
     */
    public EpubMaker(final EpubMetaData meta) {
        this.meta = meta;
    }

    /**
     * ePubを生成する.
     */
    public final void generateEpub() {
        final List<String> cleanTargets = new ArrayList<>(DEFAULT_REMOVE_TARGETS);
        final List<Pair<String, String>> pairs = new ArrayList<>(REQUIRED_METAFILE_PAIRS.toList());
        pairs.addAll(prepareContents());
        try {
            Files.write(
                Paths.get("title_page.xhtml"),
                Articles.bindArgs(
                        Resource.TITLE_PAGE,
                        new HashMap<String, String>(){
                            /** default. */
                            private static final long serialVersionUID = 1L;
                            {
                            put("date", CalendarUtil.getCurrentISODate());
                            put("title", meta.title);
                            put("subtitle", meta.subtitle);
                            put("author", meta.author);
                            put("version", meta.version);
                        }}
                ).getBytes(StandardCharsets.UTF_8)
            );
        } catch (final IOException e) {
            LOGGER.error("ERROR!", e);
        }
        archive(pairs);
        clean(cleanTargets);
    }

    /**
     * コンテンツの用意.
     * @return コンテンツ関連のファイルパスのペア
     */
    private final Collection<Pair<String, String>> prepareContents() {
        final Collection<Pair<String, String>> pairs = new ArrayList<>();
        if (contentPaths == null) {
            return null;
        }
        contentPaths.stream().forEach((path) -> {
            String parent = CONTENT_DIR;
            if (StringUtils.isNotEmpty(path.dest.toString())) {
                parent = parent.concat(path.dest.toString());
            }
            pairs.add(Tuples.pair(path.source.toString(), parent));
        });
        // navdoc.html
        generateNavdoc(contentPaths);
        // toc.ncx
        generateTocncx(contentPaths);
        // content.opf
        generateContentOpf(contentPaths);
        return pairs;
    }

    /**
     * navdoc.html(3.0用) を生成する.
     * @param fileNames
     */
    private final void generateNavdoc(final List<ContentMetaData> fileNames) {
        final StringBuilder sb = new StringBuilder();
        for (final ContentMetaData cMeta : fileNames) {
            if (!FileUtil.isImageFile(cMeta.source)) {
                sb.append("<li><a href=\"").append(Paths.get(cMeta.source).getFileName().toString())
                .append("\">")
                .append(cMeta.title)
                .append("</a></li>")
                .append(LINE_SEPARATOR);
            }
        }
        try {
            Files.write(
                    Paths.get("navdoc.html"),
                    Articles.bindArgs(
                            Resource.NAVDOC,
                            Maps.mutable.with("title", meta.title, "content", sb.toString())
                        ).getBytes(StandardCharsets.UTF_8)
                    );
        } catch (final IOException e) {
            LOGGER.error("ERROR!", e);
        }
    }

    /**
     * toc.ncx(2.0用) を生成する.
     * @param fileNames
     */
    private final void generateTocncx(final List<ContentMetaData> fileNames) {
        final StringBuilder sb = new StringBuilder();
        int order = 2;
        for (final ContentMetaData cMeta : fileNames) {
            if (!FileUtil.isImageFile(cMeta.source)) {
                sb.append(
                    "<navPoint id=\"chapter"
                    + Strings.addZero(order - 1)
                    + "\" playOrder=\""
                    + order
                    + "\"><navLabel><text>"
                );
                sb.append(cMeta.title);
                sb.append("</text></navLabel><content src=\"");
                sb.append(Paths.get(cMeta.source).getFileName().toString());
                sb.append("\"/></navPoint>");
                sb.append(LINE_SEPARATOR);
                order++;
            }
        }
        try {
            Files.write(
                    Paths.get("toc.ncx"),
                    Articles.bindArgs(
                            Resource.TOC_NCX,
                            Maps.mutable.of(
                                    "title",   meta.title,
                                    "content", sb.toString()
                                    )
                        ).getBytes(StandardCharsets.UTF_8)
                    );
        } catch (final IOException e) {
            LOGGER.error("ERROR!", e);
        }
    }

    /**
     * ファイルとタイトルの組から content.opf を生成する.
     * @param filePairs ファイルとタイトルの組
     */
    private final void generateContentOpf(final List<ContentMetaData> filePairs) {
        final StringBuilder content = new StringBuilder();
        final StringBuilder idref   = new StringBuilder();
        int contentCount = 1;
        int idrefCount = 1;
        for (final ContentMetaData cMeta : filePairs) {
            if (FileUtil.isImageFile(cMeta.source)) {
                //  <item id="imgl" href="images/koma.png" media-type="image/png" />
                content
                    .append("  <item id=\"imgl" + idrefCount + "\" href=\"")
                    .append(cMeta.entry)
                    .append("\" media-type=\"image/jpeg\" />")
                    .append(LINE_SEPARATOR);
                idrefCount++;
                continue;
            }
            content
                .append("  <item id=\"")
                .append("chapter" + Strings.addZero(contentCount))
                .append("\" href=\"")
                .append(cMeta.entry)
                .append("\" media-type=\"text/html\" />")
                //.append("\" media-type=\"application/xhtml+xml\" />")
                .append(LINE_SEPARATOR);
            idref
                .append("<itemref idref=\"")
                .append("chapter" + Strings.addZero(contentCount))
                .append("\" />")
                .append(LINE_SEPARATOR);
            contentCount++;
        }
        try {
            Files.write(
                Paths.get("content.opf"),
                Articles.bindArgs(
                        Resource.CONTENT_OPF,
                        new HashMap<String, String>(){
                            /** default. */
                            private static final long serialVersionUID = 1L;
                        {
                            put("date",      CalendarUtil.getCurrentISODate());
                            put("title",     meta.title);
                            put("author",    meta.author);
                            put("editor",    meta.editor);
                            put("publisher", meta.publisher);
                            put("content",   content.toString());
                            put("idref",     idref.toString());
                            put("ppd",       meta.direction.toString().toLowerCase());
                        }}
                ).getBytes(StandardCharsets.UTF_8)
            );
        } catch (final IOException e) {
            LOGGER.error("ERROR", e);
        }
    }

    /**
     * 渡されたファイルリストの順に圧縮する.
     * @param files
     */
    private void archive(final List<Pair<String, String>> files) {
        try {
            final ZipOutputStream out = new ZipOutputStream(new FileOutputStream(meta.zipFilePath));
            // ファイル圧縮処理
            for (final Pair<String, String> pair : files) {
                final String path  = pair.getTwo();
                final Path p = Paths.get(pair.getOne());
                if (Files.isDirectory(p)) {
                    putEntryDirectory(out, p);
                } else {
                    putEntryFile(out, pair.getOne(), path);
                }
            }
            // 出力ストリームを閉じる
            out.flush();
            out.close();
        } catch (final IOException e) {
            LOGGER.error("Caught Error.", e);
        }
        LOGGER.info("圧縮終了");
    }

    /**
     * ZipOutputStreamに対してファイルを登録する.
     * @param out ZipOutputStream
     * @param stream 登録するファイル
     * @throws FileNotFoundException
     * @throws IOException
     */
    private static void putEntryFile(
            final ZipOutputStream out,
            final String name,
            final String path
            ) throws FileNotFoundException, IOException {
        final byte[] buf = new byte[128];

        try (final BufferedInputStream in = new BufferedInputStream(FileUtil.getStream(name));) {
            // エントリを作成する
            final Path p = Paths.get(name);
            final String entryPath = path + p.getName(p.getNameCount() - 1);
            LOGGER.info(entryPath + " | " + name);
            final ZipEntry entry = new ZipEntry(entryPath);
            out.putNextEntry(entry);
            // データを圧縮して書き込む
            int size;
            while ((size = in.read(buf, 0, buf.length)) != -1) {
                out.write(buf, 0, size);
            }
            in.close();
        } catch (final FileNotFoundException e) {
            LOGGER.error("Caught FileNotFoundException.", e);
        } catch (final ZipException e) {
            LOGGER.error("Caught ZipException.", e);
        } catch (final Exception e) {
            LOGGER.error("Caught Error.", e);
        }
    }

    /**
     * ZipOutputStream に対しディレクトリを登録する.
     * @param out
     * @param path
     * @throws IOException
     */
    private static void putEntryDirectory(final ZipOutputStream out, final Path path)
            throws IOException {
        final ZipEntry entry = new ZipEntry(path.toString() + "/");
        entry.setSize(0);
        out.putNextEntry(entry);
    }

    /**
     * 不要となった生成ファイルを削除する.
     * @param pathList ファイルパスの一覧
     */
    private static final void clean(final List<String> pathList) {
        pathList.parallelStream()
            .map(Paths::get)
            .filter(Files::exists)
            .forEach(path -> {
                try {
                    Files.deleteIfExists(path);
                } catch (final Exception e) {
                    LOGGER.error("Error!", e);
                }
            });
    }

    /**
     * contentPaths をセットする.
     * @param contentPaths
     */
    public final void setContentPairs(final List<ContentMetaData> contentPaths) {
        this.contentPaths = contentPaths;
    }
}
