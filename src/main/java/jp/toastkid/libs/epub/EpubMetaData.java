package jp.toastkid.libs.epub;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonObject;

/**
 * Epub's meta data.
 * @author Toast kid
 *
 */
public final class EpubMetaData {

    /** Logger. */
    private static final Logger LOGGER = LoggerFactory.getLogger(EpubMetaData.class);

    /** Extension of ePub file. */
    public static final String EPUB_SUFFIX = ".epub";

    /** ePub's title. */
    public String title;

    /** ePub's subtitle. */
    public String subtitle;

    /** ePub's author. */
    public String author;

    /** ePub's editor. */
    public String editor;

    /** ePub's publisher. */
    public String publisher;

    /** ePub's version. */
    public String version   = "0.0.1";

    /** ePub's output path. */
    public String zipFilePath = "epub.epub";

    /** Content article's name prefix. 前方一致でマッチした記事をePubのコンテンツに含める. */
    public String targetPrefix;

    /** Targets. */
    public List<String> targets;

    /** Is contains inner link articles. */
    public boolean containInnerLinks;

    /** ePub's rule-set file name. */
    public String ruleSetFileName;

    /** 再帰的に文書を入れるか否か. */
    public boolean recursive = false;

    /** ePub's page progress direction. */
    public PageProgressDirection direction = PageProgressDirection.RTL;

    /** ePub's page layout. */
    public PageLayout layout = PageLayout.VERTICAL;

    /**
     * Initialize targets.
     */
    public EpubMetaData() {
        targets = new ArrayList<>();
    }

    @Override
    public final String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
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

    /**
     * Make {@link EpubMetaData} object from JSON string.
     *
     * @param jsonStr JSON string.
     * @return {@link EpubMetaData} object.
     */
    public static EpubMetaData readJson(final String jsonStr) {
        if (jsonStr == null) {
            return null;
        }
        final JsonObject json = Json.parse(jsonStr).asObject();

        final EpubMetaData meta = new EpubMetaData();
        meta.author             = json.getString("author", "");
        meta.containInnerLinks  = json.getBoolean("containInnerLinks", false);
        meta.direction          = PageProgressDirection.valueOf(json.getString("direction", "LTR"));
        meta.editor             = json.getString("editor", "");
        meta.layout             = PageLayout.valueOf(json.getString("layout", "HORIZONTAL"));
        meta.publisher          = json.getString("publisher", "");
        meta.recursive          = json.getBoolean("recursive", false);
        meta.ruleSetFileName    = json.getString("ruleSetFileName", "");
        meta.subtitle           = json.getString("subtitle", "");
        meta.targetPrefix       = json.getString("targetPrefix", "");
        meta.title              = json.getString("title", "");
        meta.version            = json.getString("version", "");
        meta.zipFilePath        = json.getString("zipFilePath", "");

        Optional.ofNullable(json.get("targets"))
            .ifPresent(targets -> targets.asArray().forEach(j -> meta.targets.add(j.asString())));
        return meta;
    }
}
