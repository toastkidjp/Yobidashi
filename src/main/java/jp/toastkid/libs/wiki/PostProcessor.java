package jp.toastkid.libs.wiki;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.impl.factory.Lists;
import org.eclipse.collections.impl.list.fixed.ArrayAdapter;

import jp.toastkid.gui.jfx.wiki.Functions;
import jp.toastkid.gui.jfx.wiki.models.ViewTemplate;
import jp.toastkid.libs.utils.HtmlUtil;
import jp.toastkid.libs.utils.Strings;

/**
 * make insternal links and subheadings.
 * @author Toast kid
 *
 */
public class PostProcessor {

    private static final String LINE_SEPARATOR = System.lineSeparator();

    /**
     * ひとりWiki の内部リンクを再現するための検出用正規表現.
     * ひとりWiki プラグインとの互換性を持たせるためのもの
     */
    private static final Pattern INTERNAL_LINK_PATTERN
        = Pattern.compile("\\[\\[(.+?)\\]\\]", Pattern.DOTALL);

    private static final Pattern HEADING_PATTERN
        = Pattern.compile("<h(\\d)>(.+?)</h\\d>", Pattern.DOTALL);

    /** headings. */
    private MutableList<Subheading> subheadings;

    private final String sourceDir;

    public PostProcessor(final String sourceDir) {
        this.sourceDir = sourceDir;
    }

    public String process(final String content) {

        this.subheadings = Lists.mutable.empty();

        return ArrayAdapter.adapt(content.split(LINE_SEPARATOR))
                .collect(str -> {return convertLine(str);})
                .makeString(LINE_SEPARATOR);
    }

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
            matcher = HEADING_PATTERN.matcher(str);
            if (matcher.find()) {
                final int depth = Integer.parseInt(matcher.group(1));
                final String subheading = matcher.group(2);
                final String subheadingId = extractSubheadingId(subheading);
                final String subheadTag
                    = String.format("<a id=\"%s\" href=\"#%s\">■</a>", subheadingId, subheadingId);
                str = HtmlUtil.makeHead(depth, subheadTag + subheading);
                subheadings.add(new Subheading(subheading, subheadingId, depth));
            }
        }
        return str;
    }

    /**
     * extract subheading's ID.
     * @param subheading
     * @return subheading's ID.
     */
    private String extractSubheadingId(final String subheading) {
        final String subheadingID = subheading.replace("[[", "").replace("]]", "");
        // 各ハイパーリンクプラグインの回避処理.
        // (130805) [|]
        /*if (subheading.indexOf("[") != -1 ) {
            final Matcher matcher = HYPER_LINK_PATTERN.matcher(subheadingID);
            if (matcher.find()) {
                return matcher.group(1).split("|")[0];
            }
        }

        // {wikipedia:}
        if (subheading.indexOf("ikipedia") != -1 ) {
            final Matcher matcher = WIKIPEDIA_PATTERN.matcher(subheadingID);
            if (matcher.find()) {
                return matcher.group(1);
            }
        }

        // {twitter:}
        if (subheading.indexOf("witter") != -1 ) {
            final Matcher matcher = TWITTER_PATTERN.matcher(subheadingID);
            if (matcher.find()) {
                return matcher.group(1);
            }
        }*/
        return subheadingID.trim();
    }

    /**
     * 記事名から内部リンクを生成する.
     * @param articleName
     * @return
     */
    private String makeInternalLink(String articleName) {
        // (140112) 内部リンクの追加
        String innerLink = null;
        final int lastIndexOf = articleName.lastIndexOf("#");
        if (lastIndexOf != -1) {
            innerLink = HtmlUtil.tagEscape(articleName.substring(lastIndexOf));
            articleName = articleName.substring(0, lastIndexOf);
        }

        final boolean isMd = articleName.startsWith("md:");
        if (isMd) {
            articleName = articleName.substring("md:".length());
        }

        final String bytedStr = Functions.toBytedString_EUC_JP(articleName);
        final boolean isExist
            = new File(sourceDir, bytedStr.concat(".").concat(findExtension(isMd))).exists();
        final StringBuilder generatedLink = new StringBuilder(180);
        // (121010) ソースフォルダをリンクパスに追加
        generatedLink.append("<a ");
        if (!isExist) {
            generatedLink.append("class=\"redLink\" ");
        }
        generatedLink.append("href=\"/").append(findExtension(isMd)).append("/");
        generatedLink.append(bytedStr);
        generatedLink.append(".").append(findExtension(isMd));
        if (StringUtils.isNotEmpty(innerLink)) {
            generatedLink.append(innerLink);
        }
        generatedLink.append("\">");
        generatedLink.append(articleName);
        generatedLink.append("</a>");
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
    public String generateSubheading(final ViewTemplate template) {

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
                    .append("<a href=\"#").append(subheading.id).append("\">")
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
