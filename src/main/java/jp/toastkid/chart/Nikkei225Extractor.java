package jp.toastkid.chart;

import java.io.BufferedReader;
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
 * Extract Nikkei 225 from article.
 *
 * @author Toast kid
 *
 */
public final class Nikkei225Extractor implements ChartDataExtractor {

    /** Logger. */
    private static final Logger LOGGER = LoggerFactory.getLogger(Nikkei225Extractor.class);

    /** Prefix of target article. */
    private String prefix;

    /**
     *
     * @param pathToDir source file directory
     * @param pPrefix (例) 日記2012-0
     * @return ファイル名と日経平均株価を対応させた Map
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

        for (final Path path : list) {
            final String name = Articles.decodeBytedStr(FileUtil.removeExtension(path.getFileName().toString()), Defines.TITLE_ENCODE);
            if (!name.startsWith(pPrefix)) {
                continue;
            }
            try (final BufferedReader fileReader = Files.newBufferedReader(path)) {
                String str ="";
                out:
                    while (str != null) {
                        if (str.endsWith("今日の日経平均株価終値")){
                            str = fileReader.readLine();
                            final String target = str.split("円")[0];
                            if (StringUtils.isNotEmpty(target)){
                                final String input = target.replace(",", "");
                                resultMap.put(name.replace("日記", ""), (int)Math.floor(Double.parseDouble(input)));
                                break out;
                            }
                        }
                        str = fileReader.readLine();
                    }
                    fileReader.close();
            } catch ( final IOException e) {
                LOGGER.info("Error!", e);
            }
            if (200 < resultMap.size()){
                break;
            }
        }
        return resultMap;
    }

    @Override
    public String getTitle() {
        if (prefix == null) {
            throw new IllegalStateException("'prefix' is null.");
        }
        return ChartPane.Category.NIKKEI225.text() + ": " + prefix.substring(2) + " ";
    }
}
