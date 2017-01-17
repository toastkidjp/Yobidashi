package jp.toastkid.libs.epub;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Epub's meta data.
 * @author Toast kid
 *
 */
public final class EpubMetaData {

    /** Logger. */
    private static final Logger LOGGER = LoggerFactory.getLogger(EpubMetaData.class);

    /** for use toString(). */
    private static final ObjectMapper MAPPER = new ObjectMapper();

    /** epubファイルの拡張子. */
    public static final String EPUB_SUFFIX = ".epub";

    /** タイトル. */
    public String title;

    /** サブタイトル. */
    public String subtitle;

    /** 著者. */
    public String author;

    /** 編者. */
    public String editor;

    /** 発行者. */
    public String publisher;

    /** バージョン. */
    public String version   = "0.0.1";

    /** 出力先. */
    public String zipFilePath = "epub.epub";

    /** 記事名のプレフィクス、前方一致でマッチした記事をePubのコンテンツに含める. */
    public String targetPrefix;

    public List<String> targets;

    public boolean containInnerLinks;

    public String ruleSetFileName;

    /** 再帰的に文書を入れるか否か. */
    public boolean recursive = false;

    /** ページめくり方向. */
    public PageProgressDirection direction = PageProgressDirection.RTL;

    /** ページ方向. */
    public PageLayout layout = PageLayout.VERTICAL;

    @Override
    public final String toString() {
        try {
            return MAPPER.writeValueAsString(this);
        } catch (final JsonProcessingException e) {
            e.printStackTrace();
        }
        return super.toString();
    }

    /**
     * Output rule-set to file.
     */
    public final void store() {
        final String outputName = ruleSetFileName;
        this.ruleSetFileName = null;
        try {
            Files.write(
                    Paths.get(outputName),
                    this.toString().getBytes(StandardCharsets.UTF_8)
                    );
        } catch (final IOException e) {
            LOGGER.error("ERROR!", e);
        }
    }
}
