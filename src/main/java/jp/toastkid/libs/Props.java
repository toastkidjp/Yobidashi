package jp.toastkid.libs;

import java.io.BufferedReader;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.collections.impl.block.factory.Procedures;
import org.eclipse.collections.impl.factory.Lists;

/**
 * Properties utilities.
 * @author Toast kid
 *
 */
public class Props {

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
        return readDir(new File(dirPath));
    }

    /**
     * read all properties from passed directory path.
     * @param dirPath directory path.
     * @return properties object.
     */
    public static Optional<Properties> readDir(final Path dirPath) {
        if (dirPath == null) {
            return Optional.empty();
        }
        return readDir(dirPath.toFile());
    }

    /**
     * read all properties from passed directory path.
     * @param dir directory file object.
     * @return properties object.
     */
    public static Optional<Properties> readDir(final File dir) {
        if (dir == null || !dir.exists() || !dir.canRead()) {
            return Optional.empty();
        }
        final Properties prop = new Properties();
        Lists.immutable.with(dir.listFiles()).each(
                Procedures.throwing(f -> {
                    try (final BufferedReader reader
                            = Files.newBufferedReader(f.toPath(), StandardCharsets.UTF_8)) {
                        prop.load(reader);
                    }}));
        return Optional.of(prop);
    }
}
