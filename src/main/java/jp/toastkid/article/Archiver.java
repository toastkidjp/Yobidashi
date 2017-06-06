/*
 * Copyright (c) 2017 toastkidjp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package jp.toastkid.article;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.ZonedDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jp.toastkid.libs.Zip;

/**
 * Use for simple backup.
 *
 * @author Toast kid
 *
 */
public class Archiver {

    /** Logger. */
    private static final Logger LOGGER = LoggerFactory.getLogger(Archiver.class);

    /**
     * do simpleBackup.
     * @param articleDir
     * @param offsetMs
     */
    public Path simpleBackup(final Path articleDir, final long offsetMs) {
        try {
            final Path pathToZip
                = Paths.get("backup" + ZonedDateTime.now().toInstant().getEpochSecond() + ".zip");
            final Zip backup = new Zip(pathToZip);
            Files.list(articleDir)
                .filter(path -> isBackup(path, offsetMs))
                .forEach(path -> {
                    try {
                        backup.entry(path.toAbsolutePath());
                    } catch (final IOException e) {
                        e.printStackTrace();
                    }
                });
            backup.doZip();
            return pathToZip;
        } catch (final IOException e) {
            LOGGER.error("Caught error.", e);
        }
        return null;
    }

    /**
     * Return is backup target.
     * @param path
     * @param offsetMs
     * @return
     */
    private static boolean isBackup(final Path path, final long offsetMs) {
        try {
            return offsetMs < Files.getLastModifiedTime(path).toMillis();
        } catch (final IOException e) {
            e.printStackTrace();
        }
        return false;
    }

}
