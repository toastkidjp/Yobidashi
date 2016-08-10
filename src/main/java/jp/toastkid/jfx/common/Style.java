package jp.toastkid.jfx.common;

import java.io.File;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.set.MutableSet;
import org.eclipse.collections.impl.factory.Lists;
import org.eclipse.collections.impl.factory.Sets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.application.Application;
import jp.toastkid.wiki.models.Defines;

/**
 * JavaFX stylesheet definition.
 * @author Toast kid
 * @see Application
 */
public class Style {

    /** Logger. */
    private static final Logger LOGGER = LoggerFactory.getLogger(Style.class);

    /** extension of stylesheet. */
    private static final String CSS = ".css";

    /** path to user defined stylesheets. */
    private static final String USER_DEFINED_PATH = Defines.USER_DIR + "/css/gui";

    /** default style. */
    public static final String DEFAULT = "MODENA";

    /**
     * get path to css.
     * @param s Style name.
     * @return path to css.
     */
    public static String getPath(final String s) {
        if (StringUtils.isEmpty(s)) {
            return DEFAULT;
        }
        final URL resource = Style.class.getResource("/css/" + s.toLowerCase() + CSS);
        if (resource == null) {
            final File userDefined = new File(USER_DEFINED_PATH, s.toLowerCase() + CSS);
            if (!userDefined.exists()) {
                return (resource != null) ? resource.toString() : s.toString();
            }
            try {
                return userDefined.toURI().toURL().toString();
            } catch (final MalformedURLException e) {
                LOGGER.error("Caught error.", e);
            }
        }
        return (resource != null) ? resource.toString() : s.toString();
    }

    /**
     * get path to css.
     * @param s Style name.
     * @return path to css.
     */
    public static List<String> findFileNamesFromDir() {
        final MutableSet<String> styles = Sets.mutable.empty();

        // read from jar resource.
        try {
            styles.addAll(findJarResourceDir());
        } catch (final RuntimeException e) {
            LOGGER.error("Caught error.", e);
        }

        // read from user css dir.
        styles.addAll(Lists.fixedSize.of(new File(Style.USER_DEFINED_PATH).list()));

        // select & toList
        final MutableList<String> sorted = styles
            .collect(style -> {return style.substring(0, style.lastIndexOf(".")).toUpperCase();})
            .toList();

        sorted.sort((a, b) -> {return a.compareTo(b);});
        return sorted;
    }

    /**
     * read from jar resource.
     * @return style file contained jar.
     */
    private static List<String> findJarResourceDir() {
        try {
            final URI resource = Style.class.getResource("/css/").toURI();
            if ("jar".equals(resource.getScheme())) {
                final JarURLConnection jarConn
                    = (JarURLConnection) resource.toURL().openConnection();
                final URL fileURL = jarConn.getJarFileURL();
                final FileSystem fs = FileSystems.newFileSystem(
                        Paths.get(fileURL.toURI()),
                        Style.class.getClassLoader()
                        );
                try (Stream<Path> s = Files.walk(fs.getPath("/css/"), 1)) {
                    return s.filter(p -> p.getFileName().toString().endsWith(CSS))
                            .map(   p -> p.getFileName().toString())
                            .collect(Collectors.toList());
                }
            }
            // Jar でないならパスから読み込み
             return Arrays.asList(new File(resource).list());
        } catch (final URISyntaxException | IOException e) {
            LOGGER.error("Caught error.", e);
        }
        return Lists.fixedSize.empty();
    }
}
