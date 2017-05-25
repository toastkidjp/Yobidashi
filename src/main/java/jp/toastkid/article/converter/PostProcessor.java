package jp.toastkid.article.converter;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;

import jp.toastkid.article.models.Article;
import jp.toastkid.article.models.Articles;
import jp.toastkid.libs.utils.HtmlUtil;
import jp.toastkid.libs.utils.Strings;

/**
 * make insternal links and subheadings.
 * @author Toast kid
 *
 */
public class PostProcessor {

    private static final String MD = ".md";

    /** subhead format. */
    private static final String SUBHEAD_FORMAT
        = "<a id=\"%s\" href=\"#%s\">■</a>";

    /** internal link pattern. */
    private static final Pattern INTERNAL_LINK_PATTERN
        = Pattern.compile("\\[\\[(.+?)\\]\\]", Pattern.DOTALL);

    /** HTML heading pattern. */
    private static final Pattern HEADING_PATTERN
        = Pattern.compile("<h(\\d)>(.+?)</h\\d>", Pattern.DOTALL);

    /** hyper link pattern. */
    private static final Pattern HYPER_LINK_PATTERN
        = Pattern.compile("<a .*>(.+?)</a>", Pattern.DOTALL);

    /** headings. */
    private List<Subheading> subheadings;

    /** article directory. */
    private final String articleDir;

    /**
     * initialize with article directory.
     * @param articleDir
     */
    public PostProcessor(final String articleDir) {
        this.articleDir = articleDir;
    }

    /**
     * receive request and return result.
     * @param content string
     * @return converted string with combining line separator.
     */
    public String process(final String content) {

        this.subheadings = new ArrayList<>();

        return Stream.of(content.split("\n"))
                .map(this::convertLine)
                .collect(Collectors.joining());
    }

    /**
     * convert single line.
     * @param line
     * @return converted text.
     */
    private String convertLine(final String line) {
        String str = line.intern();
        Matcher matcher;
        if (str.indexOf("[[") != -1) {
            matcher = INTERNAL_LINK_PATTERN.matcher(str);
            while (matcher.find()) {
                final String found = matcher.group(1);
                str = str.replaceFirst(INTERNAL_LINK_PATTERN.pattern(), makeInternalLink(found));
            }
        }

        // Confluence 風 Heading.
        if (str.startsWith("<h") ) {
            // for markdown.
            if (str.contains(" id=")) {
                str = str.replaceFirst(" id=\".+?\"", "");
            }
            matcher = HEADING_PATTERN.matcher(str);
            if (matcher.find()) {
                final int depth = Integer.parseInt(matcher.group(1));
                final String subheading   = matcher.group(2).trim();
                final String subheadingId = extractSubHeading(subheading).replaceAll("[ \\.\"\\?\\'\\(\\)]+", "_");
                final String subheadTag = String.format(SUBHEAD_FORMAT, subheadingId, subheadingId);
                str = HtmlUtil.makeHead(depth, subheadTag + subheading);
                subheadings.add(new Subheading(subheading, subheadingId, depth));
            }
        }
        return str;
    }

    /**
     * extract sub heading.
     * @param text
     * @return sub heading text
     */
    private String extractSubHeading(final String text) {
        if (!text.contains("<a")) {
            return text;
        }
        final Matcher matcher = HYPER_LINK_PATTERN.matcher(text);
        if (matcher.find()) {
            return matcher.group(1).trim();
        }
        return text;
    }

    /**
     * make internal link.
     * @param articleName
     * @return internal link
     */
    private String makeInternalLink(final String articleName) {
        String input = articleName;
        // (140112) 内部リンクの追加
        String innerLink = null;
        final int lastIndexOf = input.lastIndexOf("#");
        if (lastIndexOf != -1) {
            innerLink = HtmlUtil.tagEscape(input.substring(lastIndexOf));
            input = input.substring(0, lastIndexOf);
        }

        final boolean isMd = input.startsWith("md:");
        if (isMd) {
            input = input.substring("md:".length());
        }

        final String bytedStr = Articles.titleToFileName(input);
        final boolean isExist = Files.exists(Paths.get(articleDir, bytedStr.concat(MD)));
        final StringBuilder generatedLink = new StringBuilder(180);
        // (121010) ソースフォルダをリンクパスに追加
        generatedLink.append("<a ");
        if (!isExist) {
            generatedLink.append("class='redLink' ");
        }
        generatedLink
            .append("href=\"")
            .append(Article.INTERNAL_PROTOCOL)
            .append("md").append("/")
            .append(bytedStr)
            .append(MD);
        if (StringUtils.isNotEmpty(innerLink)) {
            generatedLink.append(innerLink);
        }
        generatedLink.append("\">").append(input).append("</a>");
        return generatedLink.toString();
    }

    /**
     * generate subheading html from subheadings.
     * @return subheading html.
     */
    public String generateSubheadings() {

        if (subheadings == null) {
            throw new IllegalStateException("Please could you call this method after processed.");
        }

        final StringBuilder headingHtml = new StringBuilder();
        final String tagName = "ul";

        final boolean notEmpty = StringUtils.isNotEmpty(tagName);
        if (notEmpty) {
            headingHtml.append("<").append(tagName).append(">");
        }

        if (subheadings != null) {
            subheadings.forEach(subheading ->
                headingHtml
                    .append("<li>")
                    .append("<a href=\"#")
                    .append(subheading.id).append("\">")
                    .append(subheading.title).append("</a></li>")
                    .append(Strings.LINE_SEPARATOR)
            );
        }

        if (notEmpty) {
            headingHtml.append("</").append(tagName.substring(0, 2)).append(">");
        }
        return headingHtml.toString();
    }

}
