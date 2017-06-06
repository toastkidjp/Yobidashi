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
import java.nio.file.Paths;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jp.toastkid.article.models.Article;
import jp.toastkid.libs.epub.DocToEpub;
import jp.toastkid.libs.epub.EpubMetaData;
import jp.toastkid.libs.epub.PageLayout;
import jp.toastkid.libs.epub.PageProgressDirection;
import jp.toastkid.yobidashi.models.Config;
import jp.toastkid.yobidashi.models.Defines;

/**
 * ePub generator.
 *
 * @author Toast kid
 *
 */
public class EpubGenerator {

    /** Logger. */
    private static final Logger LOGGER = LoggerFactory.getLogger(EpubGenerator.class);

    /** Config. */
    private final Config config;

    /**
     * Constructor.
     */
    public EpubGenerator(final Config config) {
        this.config = config;
    }

    /**
     * 現在開いている記事をePubに変換する.
     * @param isVertival
     * @param fileName
     */
    public final void toEpub(final Article article, final boolean isVertival) {
        final EpubMetaData meta = new EpubMetaData();
        meta.recursive = true;
        final String convertTitle = article.title;
        final String author       = config.get(Config.Key.AUTHOR);
        meta.title        = convertTitle;
        meta.subtitle     = convertTitle;
        meta.author       = author;
        meta.editor       = author;
        meta.publisher    = author;
        meta.version      = "0.0.1";
        meta.zipFilePath  = convertTitle + ".epub";
        meta.targetPrefix = convertTitle;
        meta.containInnerLinks = false;
        meta.layout       = isVertival ? PageLayout.VERTICAL : PageLayout.HORIZONTAL;
        meta.direction    = isVertival ? PageProgressDirection.RTL : PageProgressDirection.LTR;
        DocToEpub.run(meta.toString());
    }

    /**
     * DocToEpub を動かし、EPUB_RECIPE_DIR のレシピ json から複数のePubを生成する.
     */
    public final void runEpubGenerator() {
        try {
            DocToEpub.run(
                    Files.list(Paths.get(Defines.EPUB_RECIPE_DIR))
                         .map(p -> p.toAbsolutePath().toString())
                         .filter(fileName -> fileName.toLowerCase().endsWith(".json"))
                         .collect(Collectors.toList())
            );
        } catch (final IOException e) {
            LOGGER.error("Error!", e);
        }
    }

}
