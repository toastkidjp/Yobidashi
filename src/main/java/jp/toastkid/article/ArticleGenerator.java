package jp.toastkid.article;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jp.toastkid.article.converter.MarkdownConverter;
import jp.toastkid.article.converter.PostProcessor;
import jp.toastkid.article.models.Article;
import jp.toastkid.article.models.Articles;
import jp.toastkid.libs.utils.CalendarUtil;
import jp.toastkid.yobidashi.models.Config;
import jp.toastkid.yobidashi.models.Config.Key;
import jp.toastkid.yobidashi.models.Defines;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

/**
 * Article generator.
 *
 * @author Toast kid
 */
public final class ArticleGenerator {

    /** Logger. */
    private static final Logger LOGGER = LoggerFactory.getLogger(ArticleGenerator.class);

    /** background directory. */
    private static final String USER_BACKGROUND   = Defines.USER_DIR + "/res/images/background/";

    /** line separator. */
    private static final String LINE_SEPARATOR = System.lineSeparator();

    /** txt -> Wiki 変換器. */
    private final MarkdownConverter converter;

    /** Image file chooser. */
    private final ImageChooser chooser;

    private final Config config;

    /**
     * init functions.
     * @param conf
     */
    public ArticleGenerator(final Config config) {
        this.config = config;
        this.converter = Mono.<MarkdownConverter>create(
                emitter -> emitter.success(new MarkdownConverter(config))
            )
            .subscribeOn(Schedulers.elastic())
            .block();
        this.converter.openLinkBrank = true;
        chooser = new ImageChooser(USER_BACKGROUND);
    }

    /**
     * Decorate HTML content with template and CSS.
     * @param title
     * @param processed
     * @return decorated HTML content
     */
    public String decorate(final String title, final Path file) {

        final PostProcessor post = new PostProcessor(config.get(Key.ARTICLE_DIR));
        final String processed   = post.process(convertToHtml(file));
        final String subheading  = post.generateSubheadings();
        return decorate(title, processed, subheading);
    }

    /**
     * Decorate HTML content with template and CSS.
     * @param title
     * @param processed
     * @param subheading
     * @return decorated HTML content
     */
    public String decorate(final String title, final String processed, final String subheading) {
        return Articles.bindArgs(
            Defines.TEMPLATE_DIR + "/main.html",
            new HashMap<String, String>(){
                /** default uid. */
                private static final long serialVersionUID = 1L;
            {
                put("installDir",  Defines.findInstallDir());
                put("title",       title);
                put("subheadings", subheading);
                put("jarPath",     getClass().getClassLoader().getResource("assets/").toString());
                put("content",
                    new StringBuilder()
                        .append("<div class=\"content-area\">")
                        .append(LINE_SEPARATOR)
                        .append(processed)
                        .toString()
                        );
                if (Files.exists(Paths.get(USER_BACKGROUND))) {
                    final String choose = chooser.choose();
                    final String bodyAdditional = choose.isEmpty()
                            ? ""
                            : String.format("style=\"background-image: url('%s');\" ", choose);
                    put("bodyAdditional", bodyAdditional);
                } else {
                    put("bodyAdditional", "");
                }
            }
        });
    }

    /**
     * {@link MarkdownConverter} で変換した結果を返す.
     *
     * @param article Article object
     * @return 変換後の HTML 文字列
     */
    public String convertToHtml(final Article article) {
        return convertToHtml(article.path);
    }

    /**
     * {@link MarkdownConverter} で変換した結果を返す.
     * @param path Article's path
     * @return 変換後の HTML 文字列
     */
    public String convertToHtml(final Path path) {
        try {
            final long ms = Files.getLastModifiedTime(path).toMillis();
            return converter.convert(path, Article.ENCODE) + "<hr/>Last Modified： "
                    + CalendarUtil.longToStr(ms, MarkdownConverter.STANDARD_DATE_FORMAT);
        } catch (final IOException e) {
            e.printStackTrace();
            LOGGER.error("Error!", e);
        }
        return converter.convert(path , Article.ENCODE);
    }

    /**
     * Return background image URL.
     * @return image URL
     */
    public String getBackground() {
        return chooser.choose();
    }

}
