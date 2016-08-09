package jp.toastkid.gui.jfx.wiki;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import jp.toastkid.gui.jfx.wiki.models.Config;
import jp.toastkid.gui.jfx.wiki.models.Defines;
import jp.toastkid.libs.epub.DocToEpub;
import jp.toastkid.libs.epub.EpubMetaData;
import jp.toastkid.libs.epub.PageLayout;
import jp.toastkid.libs.epub.PageProgressDirection;

/**
 * ePub generator.
 *
 * @author Toast kid
 *
 */
public class EpubGenerator {

    /**
     * 現在開いている記事をePubに変換する.
     * @param isVertival
     * @param fileName
     */
    public final void toEpub(final boolean isVertival) {
        final EpubMetaData meta = new EpubMetaData();
        meta.recursive = true;
        final String convertTitle = Config.article.title;
        meta.title        = convertTitle;
        meta.subtitle     = convertTitle;
        meta.author       = Defines.AUTHOR;
        meta.editor       = Defines.AUTHOR;
        meta.publisher    = Defines.AUTHOR;
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
        final List<String> absPathes
            = Stream.of(new File(Defines.EPUB_RECIPE_DIR).listFiles())
                .map((file) -> file.getAbsolutePath())
                .filter((fileName) -> fileName.toLowerCase().endsWith(".json"))
                .collect(Collectors.toList());
        DocToEpub.run(absPathes.toArray(new String[]{}));
    }

}
