/*
 * Copyright (c) 2017 toastkidjp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package jp.toastkid.chart;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jp.toastkid.article.models.Articles;
import jp.toastkid.libs.utils.FileUtil;
import jp.toastkid.yobidashi.models.Defines;

/**
 * ファイルの文字数を計算する。
 * <HR>
 * (121113) 作成<BR>
 * @author Toast kid
 *
 */
public final class FilesLengthExtractor implements ChartDataExtractor {

    /** Logger. */
    private static final Logger LOGGER = LoggerFactory.getLogger(FilesLengthExtractor.class);

    /** 「月合計」のキー */
    public static final String TOTAL_KEY = "合計";

    /** Target file's prefix. */
    private String prefix;

    /** Overall score. */
    private long overall;

    /**
     *
     * @param pathToDir article file directory.
     * @param pPrefix (例) 日記2012-0
     * @return ファイル名とファイル内容の長さを対応させた Map
     */
    @Override
    public Map<String, Number> extract(final String pathToDir, final String pPrefix) {
        this.prefix = pPrefix;
        final Map<String, Number> resultMap = new TreeMap<>();
        final List<Path> list = new ArrayList<>();
        try {
            Files.find(Paths.get(pathToDir), 1, (p, attr) -> p.getFileName().toString().endsWith(".md"))
                    .forEach(list::add);
        } catch (final IOException e) {
            LOGGER.info("Error!", e);
        }
        long overall = 0;
        for (final Path path : list) {
            final String name = Articles.decodeBytedStr(
                    FileUtil.removeExtension(path.getFileName().toString()), Defines.TITLE_ENCODE);
            if (!name.startsWith(pPrefix)){
                continue;
            }
            final String putKey = StringUtils.isNotEmpty(pPrefix) ? name.substring(2) : name;
            final long fileCharacterValue = FileUtil.countCharacters(path);
            resultMap.put(putKey, fileCharacterValue);
            overall = overall + fileCharacterValue;
        }
        this.overall = overall;
        resultMap.put(TOTAL_KEY, overall);
        return resultMap;
    }

    @Override
    public String getTitle() {
        if (prefix == null) {
            throw new IllegalStateException("'prefix' is null.");
        }
        return String.format("%s: %s %,3d字",
                ChartPane.Category.DIARY.text(), prefix.substring(2), this.overall);
    }
}
