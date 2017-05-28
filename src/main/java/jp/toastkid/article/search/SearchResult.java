/*
 * Copyright (c) 2017 toastkidjp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package jp.toastkid.article.search;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * 記事1件の検索結果.
 *
 * @author Toast kid
 */
public final class SearchResult {

    /** File path. */
    private String filePath;

    /** Article title. */
    private String title;

    /** Article length. */
    private int length;

    /** Article's last modified ms. */
    private long lastModified;

    /** Term frequency map. */
    private Map<String, List<String>> df;

    /**
     * Constructor.
     * @param pFilePath /path/to/file
     */
    public SearchResult(final String pFilePath) {
        this.df           = new HashMap<>(20);
        this.filePath     = pFilePath;
        try {
            this.lastModified = Files.getLastModifiedTime(Paths.get(pFilePath)).toMillis();
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Make empty object.
     * @return Empty object
     */
    public static SearchResult makeSimple(final String pFilePath) {
        final SearchResult result = new SearchResult(pFilePath);
        result.put("simple", new ArrayList<>());
        return result;
    }

    public boolean mapIsEmpty() {
        return df.isEmpty();
    }

    public int size () {
        return df.size();
    }

    public String filePath() {
        return filePath;
    }

    public String title() {
        return title;
    }

    public int length() {
        return length;
    }

    public void addLength(int length) {
        this.length += length;
    }

    public long lastModified() {
        return lastModified;
    }

    public List<String> getOrEmpty(final String key) {
        return df.getOrDefault(key, new ArrayList<>());
    }

    public void put(final String key, final List<String> value) {
        df.put(key, value);
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
