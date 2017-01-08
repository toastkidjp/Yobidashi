package jp.toastkid.libs;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.collections.impl.block.factory.Procedures;
import org.eclipse.collections.impl.collector.Collectors2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Properties utilities.
 *
 * @author Toast kid
 */
public class Props {

    /** Logger. */
    private static final Logger LOGGER = LoggerFactory.getLogger(Props.class);

    /**
     * deny make instance.
     */
    private Props() {
        // Noop
    }

    /**
     * read all properties from passed directory path.
     * @param dirPath directory path.
     * @return properties object.
     */
    public static Optional<Properties> readDir(final String dirPath) {
        if (StringUtils.isBlank(dirPath)) {
            return Optional.empty();
        }
        return readDir(Paths.get(dirPath));
    }

    /**
     * read all properties from passed directory path.
     * @param dirPath directory path.
     * @return properties object.
     */
    public static Optional<Properties> readDir(final Path dirPath) {
        if (dirPath == null || !Files.exists(dirPath) || !Files.isReadable(dirPath)) {
            return Optional.empty();
        }
        final Properties prop = new Properties();
        try {
            Files.list(dirPath).collect(Collectors2.toList()).each(
                    Procedures.throwing(f -> {
                        try (final BufferedReader reader
                                = Files.newBufferedReader(f, StandardCharsets.UTF_8)) {
                            prop.load(reader);
                        }}));
        } catch (final IOException e) {
            LOGGER.error("Error!", e);
        }
        return Optional.of(prop);
    }
}
