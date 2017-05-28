/*
 * Copyright (c) 2017 toastkidjp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package jp.toastkid.jobs;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Watch file for auto backup.
 *
 * @author Toast kid
 */
public class FileWatcherJob implements Runnable {

    /** Logger. */
    private static final Logger LOGGER = LoggerFactory.getLogger(FileWatcherJob.class);

    /** File watcher task list. */
    private final Map<Path, Long> targets = new HashMap<>();

    /** Disposable. */
    private Disposable cancellation;

    @Override
    public void run() {
        final Path backup = Paths.get("backup");
        if (!Files.exists(backup) || !Files.isDirectory(backup)) {
            LOGGER.info("make backup dir.");
            try {
                Files.createDirectory(backup);
            } catch (final IOException e) {
                LOGGER.error("Error", e);
            }
        }
        cancellation = Observable.<Path>create(emitter -> {
            targets
                .entrySet()
                .stream()
                .filter(entry -> isTarget(entry.getKey(), entry.getValue()))
                .forEach(entry -> emitter.onNext(entry.getKey()));
            emitter.onComplete();
        })
        .delaySubscription(30L, TimeUnit.SECONDS)
        .repeat()
        .subscribeOn(Schedulers.newThread())
        .subscribe(path -> {
            try {
                final Path copy = Files.copy(
                        path, backup.resolve(path.getFileName().toString()),
                        StandardCopyOption.REPLACE_EXISTING
                        );
                LOGGER.info("Backup to " + copy);
                add(path);
            } catch (final IOException e) {
                LOGGER.error("Error", e);
            }
        });
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
     * @return
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
     * Add file to watch list.
     * @param path article file.
     */
    public void add(final Path path) {
        try {
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
