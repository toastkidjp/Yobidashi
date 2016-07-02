package jp.toastkid.libs.markdown;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.collections.impl.factory.Lists;

import jp.toastkid.libs.utils.CollectionUtil;

/**
 * Markdown converter.
 * <ol>
 * <li>Table
 * <li>Plugins(Ruby, tooltip, and so on...)
 * <li>Test
 * </ol>
 * @author Toast kid
 *
 */
public final class MarkdownConverter {

    /** image pattern, */
    private static final Pattern IMAGE_PATTERN
        = Pattern.compile("\\!\\[(.+?)\\]\\((.+?)\\)", Pattern.DOTALL);

    /** link pattern, */
    private static final Pattern LINK_PATTERN
        = Pattern.compile("\\[(.+?)\\]\\((.+?)\\)", Pattern.DOTALL);

    /** line separator. */
    private static final String LINE_SEPARATOR = System.lineSeparator();

    /** markdown line separator. */
    private static final String MD_LINE_SEPARATOR = "  ";

    /** html line. */
    private final List<String> htmlLines;

    /** now in ol list. */
    private boolean inOl;

    /** now in ul list. */
    private boolean inUl;

    /** now in blockquote. */
    private boolean inBq;

    /** now in code block. */
    private boolean inCode;

    /** now in paragraph. */
    private boolean inParagraph;

    /**
     * init list.
     */
    public MarkdownConverter() {
        htmlLines = new ArrayList<>();
    }

    /**
     * convert markdown string to html.
     * @param source wrote markdown
     * @return html text
     */
    public String convert(final String source) {
        return convert(source.split(LINE_SEPARATOR));
    }

    /**
     * convert markdown string to html.
     * @param split
     * @return
     */
    public String convert(final String... sourceLines) {
        return convert(Lists.immutable.with(sourceLines));
    }
    /**
     * convert markdown string to html.
     * @param split
     * @return
     */
    public String convert(final Iterable<String> sourceLines) {
        Lists.immutable.withAll(sourceLines).each(line -> {
            final String converted = convertLine(line);
            htmlLines.add(
                    StringUtils.isNotEmpty(converted) ? converted : line);
        });
        if (this.inParagraph) {
            htmlLines.add("</p>");
        }
        return CollectionUtil.implode(htmlLines, LINE_SEPARATOR);
    }

    /**
     * convert string to html.
     * @param line string line.
     * @return html string.
     */
    private String convertLine(final String line) {
        final StringBuilder sb = new StringBuilder(line.length());

        // empty str abort any special state.
        if ("".equals(line)) {
            if (this.inBq) {
                this.inBq = false;
                sb.append("</blockquote>");
            }
            if (this.inParagraph) {
                this.inParagraph = false;
                sb.append("</p>");
            }
        }

        // HR
        if (line.startsWith("-") || line.startsWith("*")) {
            if (this.inParagraph) {
                this.inParagraph = false;
                sb.append("</p>");
            }
            if (line.matches("[\\*\\- ]{3,}+")) {
                return sb.append("<hr/>").toString();
            }
        }

        // FORCE LINE separating
        if (line.endsWith(MD_LINE_SEPARATOR)) {
            sb.append(line).append("<br/>");
        }

        // hyper link
        if (line.contains("[") && line.contains("(")) {
            Matcher matcher;
            matcher = IMAGE_PATTERN.matcher(line);
            String replaceTarget = line;
            while (matcher.find()) {
                final String alt = matcher.group(1);
                final String src = matcher.group(2);
                final String replaced
                    = matcher.replaceFirst(String.format("<img alt=\"%s\" src=\"%s\">", alt, src));
                replaceTarget = replaced;
                matcher = IMAGE_PATTERN.matcher(replaceTarget);
            }
            matcher = LINK_PATTERN.matcher(replaceTarget);
            while (matcher.find()) {
                final String text = matcher.group(1);
                final String href = matcher.group(2);
                final String replaced
                    = matcher.replaceFirst(String.format("<a href=\"%s\">%s</a>", href, text));
                replaceTarget = replaced;
                matcher = LINK_PATTERN.matcher(replaceTarget);
            }
            sb.append(replaceTarget);
        }

        // Heading
        if (line.startsWith("#")) {
            if (line.startsWith("######")) {
                sb.append(line.replaceFirst(".*#", "<h6>")).append("</h6>");
            } else if (line.startsWith("#####")) {
                sb.append(line.replaceFirst(".*#", "<h5>")).append("</h5>");
            } else if (line.startsWith("####")) {
                sb.append(line.replaceFirst(".*#", "<h4>")).append("</h4>");
            } else if (line.startsWith("###")) {
                sb.append(line.replaceFirst(".*#", "<h3>")).append("</h3>");
            } else if (line.startsWith("##")) {
                sb.append(line.replaceFirst(".*#", "<h2>")).append("</h2>");
            } else if (line.startsWith("#")) {
                sb.append(line.replaceFirst(".*#", "<h1>")).append("</h1>");
            }
            return sb.toString();
        }

        // 数値付きリスト
        if (line.matches("^\\d\\. ")) {
            if (!this.inOl) {
                this.inOl = true;
                sb.append("<ol>");
            }
            sb.append(line.replaceFirst("^\\d\\. ", "<li>")).append("</li>");
        } else {
            if (this.inOl) {
                sb.append("</ol>");
            }
            this.inOl = false;
        }

        // 数値付きリスト
        if (line.matches("^\\* ")) {
            if (!this.inUl) {
                this.inUl = true;
                sb.append("<ul>");
            }
            sb.append(line.replaceFirst("^\\* ", "<li>")).append("</li>");
        } else {
            if (this.inUl) {
                sb.append("</ul>");
            }
            this.inUl = false;
        }

        // 引用
        if (line.startsWith(">")) {
            if (!this.inBq) {
                this.inBq = true;
                return sb.append("<blockquote>").append(line.substring(1)).toString();
            }
        }

        // code block
        if ("```".equals(line)) {
            if (this.inCode) {
                sb.append("</pre>");
                this.inCode = false;
            } else {
                sb.append("<pre class=\"code\">");
                this.inCode = true;
            }
        }

        // paragraph
        if (!(this.inBq || this.inCode || this.inOl || this.inUl) && !this.inParagraph) {
            this.inParagraph = true;
            sb.append("<p>").append(line);
        }


        return sb.toString();
    }

}
