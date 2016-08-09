package jp.toastkid.libs.markdown;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;

import jp.toastkid.gui.jfx.wiki.ArticleGenerator;
import jp.toastkid.gui.jfx.wiki.models.Config;
import jp.toastkid.gui.jfx.wiki.models.Defines;
import jp.toastkid.gui.jfx.wiki.models.ViewTemplate;
import jp.toastkid.libs.utils.Strings;

import org.junit.Test;

/**
 * {@link MarkdownConverter}'s test.
 * @author Toast kid
 *
 */
public class MarkdownConverterTest {

    private static final File FILE = new File("src/test/resources/libs/markdown/source.md");

    /**
     * check {@link MarkdownConverter#convert(String)}.
     * @throws IOException
     */
    @Test
    public final void testConvert() throws IOException {
        final List<String> source = Files.readAllLines(FILE.toPath(), StandardCharsets.UTF_8);
        source.add("");
        source.add("----");
        source.add("最終更新： " + Strings.toYmdhmsse(FILE.lastModified()));

        final String content = new MarkdownConverter().convert(source);

        final String html = ArticleGenerator.bindArgs(
            ViewTemplate.MATERIAL.getPath(),
            new HashMap<String, String>(){
                /** default uid. */
                private static final long serialVersionUID = 1L;
            {
                put("installDir", Defines.findInstallDir());
                put("title",      "test");
                put("wikiIcon",   Config.get("wikiIcon"));
                put("wikiTitle",  Config.get("wikiTitle", "Wiklone"));
                put("content",
                    new StringBuilder().append("<div class=\"body\">")
                        .append(System.lineSeparator())
                        .append("<div class=\"content_area\">")
                        .append(System.lineSeparator())
                        .append(content)
                        .append("</div>")
                        .toString()
                        );
            }});
        System.out.println(html);
    }

}
