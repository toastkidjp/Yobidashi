/*
 * Copyright (c) 2017 toastkidjp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package jp.toastkid.jobs;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Watch file for auto backup.
 *
 * @author Toast kid
 */
public class FileWatcherJob {

    /** Logger. */
    private static final Logger LOGGER = LoggerFactory.getLogger(FileWatcherJob.class);

    /** File watcher targets. */
    private final Map<Path, Long> targets;

    /** Disposable. */
    private Disposable cancellation;

    /** Backup file directory. */
    private static final Path BACKUP_DIRECTORY = Paths.get("backup");

    /**
     * Initialize map.
     */
    public FileWatcherJob() {
        targets = new HashMap<>();
    }

    /**
     * Start watcher.
     */
    public void start() {
        makeDirIfNotExists();
        cancellation = Observable.interval(30L, TimeUnit.SECONDS)
                .map(l -> targets
                        .entrySet()
                        .stream()
                        .filter(entry -> isTarget(entry.getKey(), entry.getValue()))
                        .map(Map.Entry::getKey)
                        .collect(Collectors.toList())
                )
                .flatMap(Observable::fromIterable)
                .subscribeOn(Schedulers.newThread())
                .subscribe(this::copyToBackup);
    }

    /**
     * Make backup files directory.
     */
    private void makeDirIfNotExists() {
        if (Files.exists(BACKUP_DIRECTORY) && Files.isDirectory(BACKUP_DIRECTORY)) {
            return;
        }

        LOGGER.info("make backup dir.");
        try {
            Files.createDirectory(BACKUP_DIRECTORY);
        } catch (final IOException e) {
            LOGGER.error("Error", e);
        }
    }

    /**
     * Copy target file to backup folder.
     * @param path target file
     */
    private void copyToBackup(final Path path) {
        try {
            final Path copy = Files.copy(
                    path,
                    BACKUP_DIRECTORY.resolve(path.getFileName().toString()),
                    StandardCopyOption.REPLACE_EXISTING
                    );
            LOGGER.info("Backup to " + copy);
            refresh(path);
        } catch (final IOException e) {
            LOGGER.error("Error", e);
        }
    }

    /**
     * For testing.
     */
    public void stop() {
        cancellation.dispose();
    }

    /**
     * Is passed path job's target?
     * @param path
     * @param ms
     * @return Is this path and ms pair target?
     */
    private boolean isTarget(final Path path, final long ms) {
        try {
            return ms < Files.getLastModifiedTime(path).toMillis();
        } catch (final IOException e) {
            LOGGER.error("Error", e);
            targets.remove(path);
        }
        return false;
    }

    /**
     * Refresh timestamp.
     * @param path article file.
     */
    private void refresh(final Path path) {
        try {
            targets.put(path, Files.getLastModifiedTime(path).toMillis());
        } catch (IOException e) {
            LOGGER.error("Error", e);
        }
    }

    /**
     * Add file to watch list.
     * @param path article file.
     */
    public void add(final Path path) {
        if (targets.containsKey(path)) {
            return;
        }
        try {
            LOGGER.info("Add backup target: {}", path.getFileName());
            targets.put(path, Files.getLastModifiedTime(path).toMillis());
        } catch (final IOException e) {
            LOGGER.error("Error", e);
        }
    }

    /**
     * Remove Path to list.
     * @param path
     */
    public void remove(final Path path) {
        targets.remove(path);
    }

    /**
     * Clear watch list.
     */
    public void clear() {
        targets.clear();
    }

}
