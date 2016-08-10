package jp.toastkid.libs.wiki;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.impl.factory.Lists;
import org.eclipse.collections.impl.list.fixed.ArrayAdapter;

import jp.toastkid.libs.utils.HtmlUtil;
import jp.toastkid.libs.utils.Strings;
import jp.toastkid.wiki.ArticleGenerator;
import jp.toastkid.wiki.models.ViewTemplate;

/**
 * make insternal links and subheadings.
 * @author Toast kid
 *
 */
public class PostProcessor {

    /** subhead format. */
    private static final String SUBHEAD_FORMAT
        = "<a class='waves-attach waves-effect' id=\"%s\" href=\"#%s\">■</a>";

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
    private MutableList<Subheading> subheadings;

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

        this.subheadings = Lists.mutable.empty();

        return ArrayAdapter.adapt(content.split("\n"))
                .collect(this::convertLine)
                .makeString("");
    }

    /**
     * convert single line.
     * @param line
     * @return converted text.
     */
    private String convertLine(final String line) {
        String str = new String(line.toString());
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
                final String subheadingId = extractSubHeading(subheading);
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

        final String bytedStr = ArticleGenerator.toBytedString_EUC_JP(input);
        final boolean isExist
            = new File(articleDir, bytedStr.concat(".").concat(findExtension(isMd))).exists();
        final StringBuilder generatedLink = new StringBuilder(180);
        // (121010) ソースフォルダをリンクパスに追加
        generatedLink.append("<a class='waves-attach waves-effect ");
        if (!isExist) {
            generatedLink.append("redLink");
        }
        generatedLink.append("' ")
            .append("href=\"/").append(findExtension(isMd)).append("/")
            .append(bytedStr)
            .append(".").append(findExtension(isMd));
        if (StringUtils.isNotEmpty(innerLink)) {
            generatedLink.append(innerLink);
        }
        generatedLink.append("\">").append(input).append("</a>");
        return generatedLink.toString();
    }

    /**
     * if true. return md. else return txt.
     * @param isMd
     * @return.
     */
    private String findExtension(final boolean isMd) {
        return isMd ? "md" : "txt";
    }

    /**
     * generate subheading html from subheadings.
     * @return subheading html.
     */
    public String generateSubheadings(final ViewTemplate template) {

        if (subheadings == null) {
            throw new IllegalStateException("Please could you call this method after processed.");
        }

        final StringBuilder headingHtml = new StringBuilder();
        final String tagName = getTag(template);

        final boolean notEmpty = StringUtils.isNotEmpty(tagName);
        if (notEmpty) {
            headingHtml.append("<").append(tagName).append(">");
        }

        if (subheadings != null) {
            subheadings.each((subheading) -> {
                headingHtml
                    .append("<li>")
                    .append("<a class='waves-attach waves-effect' href=\"#")
                    .append(subheading.id).append("\">")
                    .append(subheading.title).append("</a>").append("<br/>")
                    .append(Strings.LINE_SEPARATOR);
            });
        }

        if (notEmpty) {
            headingHtml.append("</").append(tagName.substring(0, 2)).append(">");
        }
        return headingHtml.toString();
    }

    /**
     * return list tag name.
     * @param template
     * @return
     */
    private String getTag(final ViewTemplate template) {
        switch (template.toString()) {
            case "MATERIAL":
                return "ul class=\"nav\"";
            default:
                return "ol";
        }
    }

}
