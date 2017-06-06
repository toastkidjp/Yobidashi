/*
 * Copyright (c) 2017 toastkidjp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package jp.toastkid.jfx.common;

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
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.application.Application;
import jp.toastkid.libs.utils.FileUtil;
import jp.toastkid.libs.utils.Strings;
import jp.toastkid.yobidashi.models.Defines;

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
     * Private constructor.
     */
    private Style() {
        // NOP.
    }

    /**
     * Get path to css.
     * @param styleName Style name.
     * @return path to css.
     */
    public static String getPath(final String styleName) {
        if (StringUtils.isEmpty(styleName)) {
            return DEFAULT;
        }
        final URL resource = Style.class.getResource("/css/" + styleName.toLowerCase() + CSS);
        if (resource == null) {
            final Path userDefined = Paths.get(USER_DEFINED_PATH, styleName.toLowerCase() + CSS);
            if (!Files.exists(userDefined)) {
                return (resource != null) ? resource.toString() : styleName.toString();
            }
            try {
                return userDefined.toUri().toURL().toString();
            } catch (final MalformedURLException e) {
                LOGGER.error("Caught error.", e);
            }
        }
        return (resource != null) ? resource.toString() : styleName.toString();
    }

    /**
     * Find file name from css dir.
     * @return list of css names.
     */
    public static List<String> findFileNamesFromDir() {
        final Set<String> styles = new HashSet<>();

        // read from jar resource.
        try {
            styles.addAll(findJarResourceDir());
        } catch (final RuntimeException e) {
            LOGGER.error("Caught error.", e);
        }

        // read from user css dir.
        final Path userDir = Paths.get(Style.USER_DEFINED_PATH);
        if (Files.exists(userDir) && Files.isDirectory(userDir)) {
            try {
                Files.find(userDir, 1, Style::isValidItem)
                     .map(Path::getFileName)
                     .map(Path::toString)
                     .forEach(styles::add);
            } catch (final IOException e) {
                LOGGER.error("Caught error.", e);
            }
        }

        // select & toList
        final List<String> sorted = styles.stream()
                .filter(style -> style.contains("."))
                .map(style -> style.substring(0, style.lastIndexOf(".")).toUpperCase())
                .collect(Collectors.toList());

        sorted.sort((a, b) -> a.compareTo(b));
        return sorted;
    }

    /**
     * For use in lambda.
     * @param path
     * @param attr
     * @return
     */
    private static boolean isValidItem(final Path path, final BasicFileAttributes attr) {
        final Optional<String> findExtension = FileUtil.findExtension(path);
        if (!findExtension.isPresent()) {
            return false;
        }
        return ".css".equals(findExtension.orElseGet(Strings::empty));
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
                try (final Stream<Path> s = Files.walk(fs.getPath("/css/"), 1)) {
                    return s.filter(p -> p.getFileName().toString().endsWith(CSS))
                            .map(   p -> p.getFileName().toString())
                            .collect(Collectors.toList());
                }
            }
            // Jar でないならパスから読み込み
            return Files.list(Paths.get(resource))
                     .map(p -> p.getFileName().toString())
                     .collect(Collectors.toList());
        } catch (final URISyntaxException | IOException e) {
            LOGGER.error("Caught error.", e);
        }
        return Collections.emptyList();
    }
}
