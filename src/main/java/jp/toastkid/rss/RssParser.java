package jp.toastkid.rss;

import java.net.URI;
import java.util.Arrays;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.collections.impl.factory.Lists;

import jp.toastkid.libs.http.Http;
import jp.toastkid.libs.utils.HtmlUtil;
import jp.toastkid.rss.Rss.Item;

/**
 * RSS Parser.
 *
 * @author Toast kid
 *
 */
public class RssParser {

    /** pattern of description. */
    private static final Pattern DESCRIPTION_PATTERN
        = Pattern.compile("<description>(.+?)</description>", Pattern.DOTALL);

    /** pattern of link. */
    private static final Pattern LINK_PATTERN
        = Pattern.compile("<link>(.+?)</link>", Pattern.DOTALL);

    /** pattern of creator. */
    private static final Pattern CREATOR_PATTERN
        = Pattern.compile("<dc:creator>(.+?)</dc:creator>", Pattern.DOTALL);

    /** pattern of date. */
    private static final Pattern DATE_PATTERN
        = Pattern.compile("<dc:date>(.+?)</dc:date>", Pattern.DOTALL);

    /** pattern of subject. */
    private static final Pattern SUBJECT_PATTERN
        = Pattern.compile("<dc:subject>(.+?)</dc:subject>", Pattern.DOTALL);

    /** pattern of pubDate. */
    private static final Pattern PUBDATE_PATTERN
        = Pattern.compile("<pubDate>(.+?)</pubDate>", Pattern.DOTALL);

    /** current extracting item. */
    private final Rss rss;

    /** current extracting item. */
    private Item currentItem;

    /** is processing content. */
    private boolean isInContent;

    /**
     * RSS parser.
     */
    public RssParser() {
        this.rss = new Rss();
    }

    /**
     * parse rss.
     * @param rss String.
     * @return Rss items.
     */
    public Rss parse(final URI uri) {
        return parse(
                Http.GET.url(uri.toString())
                    .fetchOpt()
                    .map(response -> Arrays.asList(response.text().split(System.lineSeparator())))
                    .orElse(Collections.emptyList())
                );
    }

    /**
     * parse rss.
     * @param rss String.
     * @return Rss items.
     */
    public Rss parse(final String rss) {
        return parse(Lists.immutable.with(rss.split(System.lineSeparator())));
    }

    /**
     * parse rss.
     * @param rss string iterable
     * @return Rss items.
     */
    public Rss parse(final Iterable<String> rssLines) {
        if (rss == null) {
            return Rss.empty();
        }
        Lists.immutable.withAll(rssLines).each(line -> {
            //System.out.println("" + line);
            if (line.contains("<item")) {
                init();
            }
            if (line.contains("<title")) {
                extractTitle(line);
            }
            if (line.contains("<link")) {
                extractLink(line);
            }
            if (line.contains("<description")) {
                extractDescription(line);
            }
            if (line.contains("</content")) {
                isInContent = false;
            }
            if (line.contains("<content")) {
                isInContent = true;
            }
            if (isInContent) {
                if (rss == null) {
                    init();
                }
                currentItem.addContent(line);
            }
            if (line.contains("<pubDate")) {
                extractPubDate(line);
            }
            if (line.contains("<dc:creator>")) {
                extractCreator(line);
            }
            if (line.contains("<dc:date>")) {
                extractDate(line);
            }
            if (line.contains("<dc:subject>")) {
                extractSubject(line);
            }
            if (line.contains("</item>")) {
                rss.addItem(currentItem);
            }
        });
        return this.rss;
    }

    /**
     * Extract pubData.
     * @param line
     */
    private void extractPubDate(final String line) {
        if (rss != null) {
            rss.setDate(extract(line, PUBDATE_PATTERN));
        }
    }

    /**
     * Extract date.
     * @param line
     */
    private void extractDate(final String line) {
        if (rss != null) {
            rss.setDate(extract(line, DATE_PATTERN));
        }
    }

    /**
     * Extract creator.
     * @param line
     */
    private void extractCreator(final String line) {
        if (rss != null) {
            rss.setCreator(extract(line, CREATOR_PATTERN));
        }
    }

    /**
     * Extract subject.
     * @param line
     */
    private void extractSubject(final String line) {
        if (rss != null) {
            rss.addSubjects(extract(line, SUBJECT_PATTERN));
        }
    }

    /**
     * extract title from html.
     * @param line
     */
    private void extractTitle(final String line) {
        final String title = HtmlUtil.extractTitle(line);
        if (currentItem == null) {
            rss.setTitle(title);
            return;
        }

        currentItem.setTitle(title);
    }

    /**
     * Extract description.
     * @param line
     */
    private void extractDescription(final String line) {
        final String description = extract(line, DESCRIPTION_PATTERN);
        if (currentItem == null) {
            rss.setDescription(description);
            return;
        }

        currentItem.setDescription(description);
    }

    /**
     * Extract link.
     * @param line
     */
    private void extractLink(final String line) {
        final String link = extract(line, LINK_PATTERN);
        if (currentItem == null) {
            rss.setLink(link);
            return;
        }
        currentItem.setLink(link);
    }

    /**
     * extract string with passed pattern.
     * @param line string line.
     * @param pattern pattern.
     * @return string
     */
    private String extract(final String line, final Pattern pattern) {
        if (StringUtils.isBlank(line) || pattern == null) {
            return line;
        }
        final Matcher matcher = pattern.matcher(line);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return line;
    }

    /**
     * init Item.
     */
    private void init() {
        currentItem = new Rss.Item();
    }

}
