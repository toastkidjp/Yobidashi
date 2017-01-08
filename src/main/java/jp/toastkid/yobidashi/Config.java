package jp.toastkid.yobidashi;

import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Properties;

import org.eclipse.collections.impl.factory.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jp.toastkid.libs.Props;

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
    private static final Logger LOGGER = LoggerFactory.getLogger(Config.class);

    /**
     * Keys of Config.
     * @author Toast kid
     *
     */
    public enum Key {
        AUTHOR("author"),
        APP_TITLE("appTitle"),
        ARTICLE_DIR("articleDir"),
        STYLESHEET("stylesheet"),
        IMAGE_DIR("imageDir");

        /** Key of Config. */
        private final String text;

        /**
         * Set text.
         * @param text
         */
        private Key(final String text) {
            this.text = text;
        }
    }

    /** Message in properties file. */
    private static final String MESSAGE = "Tool's Property.";

    /** Configrations. */
    private static final Properties CONFIG = new Properties();
    static {
        reload();
    }

    /**
     * Get property value by key.
     * @param key string
     * @return property value object
     */
    public static final Object getByObject(final String key) {
        return CONFIG.getProperty(key, "");
    }

    /**
     * Get property value by key.
     * @param key string
     * @return property value object
     */
    public static final String get(final String key) {
        return get(key, "");
    }

    /**
     * Get property value by key.
     * @param key key
     * @return property value object
     */
    public static final String get(final Key key) {
        return get(key.text, "");
    }

    /**
     * Get property value by key.
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

    /**
     * 設定を再読み込みする.
     */
    public static void reload() {

        final Path userDir = Paths.get(Defines.USER_DIR + "/conf");
        if (!Files.exists(userDir)) {
            try {
                Files.createDirectories(userDir);
                store();
            } catch (final IOException e) {
                LOGGER.error("Error!", e);
            }
        }

        CONFIG.clear();
        CONFIG.putAll(Props.readDir(Defines.CONF_DIR).get());
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
                = Files.newBufferedWriter(Paths.get(Defines.CONF_DIR, Defines.CONF_NAME))) {
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
