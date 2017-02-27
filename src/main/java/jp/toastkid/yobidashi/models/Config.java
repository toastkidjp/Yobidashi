package jp.toastkid.yobidashi.models;

import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.collections.impl.factory.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * config of Wiki client.
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
        APP_ICON("appIcon"),
        ARTICLE_DIR("articleDir"),
        STYLESHEET("stylesheet"),
        IMAGE_DIR("imageDir"),
        FONT_SIZE("fontSize"),
        FONT_FAMILY("fontFamily"),
        TEST("test"),
        YID("yid");

        /** Key of Config. */
        private final String text;

        /**
         * Set text.
         * @param text
         */
        private Key(final String text) {
            this.text = text;
        }

        /**
         * Return text.
         * @return text
         */
        public String text() {
            return this.text;
        }
    }

    /** Message in properties file. */
    private static final String MESSAGE = "Tool's Property.";

    /** Configurations. */
    private final Properties config = new Properties();

    /** path/to/userDir.  */
    private final Path path;

    /** Configuration file's encode. */
    public static final String ENCODE = "UTF-8";

    /**
     * Initialize with path.
     * @param userDir
     */
    public Config(final Path userDir) {
        this.path = userDir;
        reload();
    }

    /**
     * Get property value by key.
     * @param key key
     * @return property value object
     */
    public final String get(final Key key) {
        return get(key, "");
    }

    /**
     * Get property value by key.
     * @param key key
     * @return property value object
     */
    public final String get(final Key key, final String substitute) {
        return config.getProperty(key.text, substitute).toString();
    }

    /**
     * Get property int value by key.
     * @param key key
     * @return property value object
     */
    public final int getInt(final Key key, final int substitute) {
        if (!config.containsKey(key.text)) {
            return substitute;
        }
        final String string = get(key).toString();
        if (StringUtils.isBlank(string)) {
            return substitute;
        }
        return Integer.parseInt(string);
    }

    /**
     * Reload config.
     */
    public void reload() {
        if (!Files.exists(path)) {
            try {
                Files.createDirectories(path);
                store();
            } catch (final IOException e) {
                LOGGER.error("Error!", e);
            }
        }

        config.clear();
        final Properties p = new Properties();
        try (final InputStream reader = Files.newInputStream(path)) {
            p.load(reader);
        } catch (final IOException e) {
            LOGGER.error("Occurred error!", e);
        }
        config.putAll(p);
    }

    /**
     * store configurations to file.
     */
    public void store() {
        store(Maps.fixedSize.empty());
    }

    /**
     * store configurations with passed pairs to file.
     * @param additional key-value pairs.
     */
    public void store(final Map<String, String> additional) {
        config.putAll(additional);
        try (final Writer writer = Files.newBufferedWriter(path)) {
            config.store(writer, MESSAGE);
        } catch (final IOException e) {
            LOGGER.error("Caught error.", e);
        }
    }

    /**
     * store configurations with passed single pair to file.
     * @param key
     * @param value
     */
    public void store(final Key key, final String value) {
        store(Maps.fixedSize.of(key.text, value));
    }

}
