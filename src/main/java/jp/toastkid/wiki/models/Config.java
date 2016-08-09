package jp.toastkid.wiki.models;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.collections.impl.factory.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jp.toastkid.libs.Props;
import jp.toastkid.libs.utils.FileUtil;
import jp.toastkid.yobidashi.Main;

/**
 * config of Wiki client.
 *
 * <ol>
 * <li>Config……可変値
 * <li>Define……固定値
 * </ol>
 * <HR>
 *
 * @author Toast kid
 * @version 0.0.1
 */
public final class Config {

    /** Logger. */
    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    /**
     * keys of Config.
     * @author Toast kid
     *
     */
    public enum Key {
        WIKI_TITLE("wikiTitle"),
        EDITOR_PATH("editorPath"),
        ARTICLE_DIR("articleDir"),
        STYLESHEET("stylesheet"),
        VIEW_TEMPLATE("viewTemplate"),
        SLIDE_THEME("slideTheme"),
        HOME("home"),
        MUSIC_DIR("musicDir"),
        IMAGE_DIR("imageDir");

        /** key of Config. */
        private final String text;

        /**
         * set text.
         * @param text
         */
        private Key(final String text) {
            this.text = text;
        }
    }

    /** message in properties file. */
    private static final String MESSAGE = "Tool's Property.";

    /** configrations. */
    private static final Properties CONFIG = new Properties();
    static {
        reload();
    }

    /** path/to/editor. contains option. */
    private static String editorPath;

    /** 現在選択中の記事. */
    public static Article article;

    /**
     * get property value by key.
     * @param key string
     * @return property value object
     */
    public static final Object getByObject(final String key) {
        return CONFIG.getProperty(key, "");
    }

    /**
     * get property value by key.
     * @param key string
     * @return property value object
     */
    public static final String get(final String key) {
        return get(key, "");
    }

    /**
     * get property value by key.
     * @param key key
     * @return property value object
     */
    public static final String get(final Key key) {
        return get(key.text, "");
    }

    /**
     * get property value by key.
     * @param key key
     * @return property value object
     */
    public static final String get(final Key key, final String substitute) {
        return get(key.text, substitute);
    }

    /**
     * get property value by key.
     * @param key string
     * @param substitute default value
     * @return property value object
     */
    public static final String get(final String key, final String substitute) {
        return CONFIG.getProperty(key, substitute).toString();
    }

    /** リロードボタンの画像へのパス. */
    //private static final String IMAGE_RELOAD = "reload.png";


    /**
     * 設定をファイルに保存する.
     * @param key   設定のキー
     * @param value 設定値
     */
    public final void storeConfig(
            final String key,
            final String value
            ) {
        final Map<String,String> confMap = FileUtil.isExistFile(Defines.CONF_DIR)
                ? FileUtil.createMapFromFile(Defines.CONF_DIR, Defines.CONF_ENCODE, "=")
                : new HashMap<String,String>(10);
        if (StringUtils.isNotEmpty(key)){
            confMap.put(key, value);
        }
        FileUtil.outPutMap(
                confMap,
                Defines.CONF_DIR,
                Defines.CONF_ENCODE,
                "="
        );
    }
    /**
     * 設定を再読み込みする.
     */
    public static void reload() {
        CONFIG.clear();
        CONFIG.putAll(Props.readDir(Defines.CONF_DIR).get());
        editorPath = get(Key.EDITOR_PATH);
        if (editorPath.toLowerCase().indexOf("terapad") != -1) {
            editorPath = editorPath + Defines.TERAPAD_OPTIONS;
        }
    }

    /**
     * store configurations to file.
     */
    public static void store() {
        store(Maps.fixedSize.empty());
    }

    /**
     * store configurations with passed pairs to file.
     * @param additional key-value pairs.
     */
    public static void store(final Map<String, String> additional) {
        CONFIG.putAll(additional);
        try (final Writer writer
                = Files.newBufferedWriter(new File(Defines.CONF_DIR, Defines.CONF_NAME).toPath())) {
            CONFIG.store(writer, MESSAGE);
        } catch (final IOException e) {
            LOGGER.error("Caught error.", e);
        }
    }

    /**
     * store configurations with passed single pair to file.
     * @param key
     * @param value
     */
    public static void store(final Key key, final String value) {
        store(Maps.fixedSize.of(key.text, value));
    }

    /**
     * store configurations with passed single pair to file.
     * @param key
     * @param value
     */
    public static void store(final String key, final String value) {
        store(Maps.fixedSize.of(key, value));
    }

}
