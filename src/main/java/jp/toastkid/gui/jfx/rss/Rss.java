package jp.toastkid.gui.jfx.rss;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.collections.impl.factory.Lists;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jp.toastkid.libs.http.Http;
import jp.toastkid.libs.utils.HtmlUtil;

/**
 * TODO write test.
 * @author Toast kid
 *
 */
public class Rss {

    /** pattern of description. */
    private static final Pattern DESCRIPTION_PATTERN = Pattern.compile(
            "<description>(.+?)</description>",
            Pattern.DOTALL
        );

    /** pattern of link. */
    private static final Pattern LINK_PATTERN = Pattern.compile(
            "<link>(.+?)</link>",
            Pattern.DOTALL
        );

    /** pattern of creator. */
    private static final Pattern CREATOR_PATTERN = Pattern.compile(
            "<dc:creator>(.+?)</dc:creator>",
            Pattern.DOTALL
        );

    /** pattern of date. */
    private static final Pattern DATE_PATTERN = Pattern.compile(
        "<dc:date>(.+?)</dc:date>",
        Pattern.DOTALL
    );

    /** pattern of subject. */
    private static final Pattern SUBJECT_PATTERN = Pattern.compile(
        "<dc:subject>(.+?)</dc:subject>",
        Pattern.DOTALL
    );

    /** pattern of pubDate. */
    private static final Pattern PUBDATE_PATTERN = Pattern.compile(
            "<pubDate>(.+?)</pubDate>",
            Pattern.DOTALL
        );

    /**
     * RSS's item.
     * @author Toast kid
     *
     */
    public class Item {
        public String title;
        public String link;
        public List<String> subjects;
        public String creator;
        public String date;
        public String description;
        public StringBuilder content;

        /**
         * initialize list.
         */
        public Item() {
            this.subjects = new ArrayList<>();
            this.content  = new StringBuilder();
        }
    }

    /** rss items. */
    private final List<Item> items;

    /** current extracting item. */
    private Item item;

    /** target url string. */
    private final String url;

    /** is processing content. */
    private boolean isInContent;

    /** current title. */
    private String title;

    /**
     * get title.
     * @return title
     */
    public String getTitle() {
        return title;
    }

    private String link;

    public String getLink() {
        return link;
    }

    private String description;

    public String getDescription() {
        return description;
    }


    /**
     * initialize with rss's url.
     * @param url rss's url
     * @return rss items.
     */
    public Rss(final String url) {
        this.url = url;
        items = new ArrayList<Rss.Item>();
    }

    /**
     * parse rss with fetch from url.
     * @return Rss items.
     */
    public List<Item> parse() {
        return parse(Http.GET.url(this.url).fetch().text());
    }

    /**
     * parse rss.
     * @param rss String.
     * @return Rss items.
     */
    public List<Item> parse(final String rss) {
        return parse(Lists.immutable.with(rss.split(System.lineSeparator())));
    }

    /**
     * parse rss.
     * @param rss string iterable
     * @return Rss items.
     */
    public List<Item> parse(final Iterable<String> rss) {
        if (rss == null) {
            return Collections.emptyList();
        }
        Lists.immutable.withAll(rss).each(line -> {
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
                if (item == null) {
                    init();
                }
                item.content.append(line);
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
                getItems().add(item);
            }
        });
        return getItems();
    }

    private void extractPubDate(final String line) {
        if (item == null) {
            return;
        }
        item.date = extract(line, PUBDATE_PATTERN);
    }

    private void extractDate(final String line) {
        if (item == null) {
            return;
        }
        item.date = extract(line, DATE_PATTERN);
    }

    private void extractCreator(final String line) {
        if (item == null) {
            return;
        }
        item.creator = extract(line, CREATOR_PATTERN);
    }

    private void extractSubject(final String line) {
        if (item == null) {
            return;
        }
        item.subjects.add(extract(line, SUBJECT_PATTERN));
    }

    /**
     * extract title from html.
     * @param line
     */
    private void extractTitle(final String line) {
        final String title = HtmlUtil.extractTitle(line);
        if (item == null) {
            this.title = title;
        } else {
            item.title = title;
        }
    }

    private void extractDescription(final String line) {
        final String description = extract(line, DESCRIPTION_PATTERN);
        if (item == null) {
            this.description = description;
        } else {
            item.description = description;
        }
    }

    private void extractLink(final String line) {
        final String link = extract(line, LINK_PATTERN);
        if (item == null) {
            this.link = link;
        } else {
            item.link = link;
        }
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

    public String expandTitle() {
        if (StringUtils.isBlank(this.url)) {
            return this.url;
        }
        try {
            final URL u = new URL(this.url);
            return u.getHost() + u.getPath();
        } catch (final MalformedURLException e) {
            e.printStackTrace();
        }
        return this.url;
    }

    /**
     * init Item.
     */
    private void init() {
        item = new Item();
    }

    /**
     * return items.
     * @return
     */
    public List<Item> getItems() {
        return items;
    }

    /**
     * main method.
     * @param args
     * @throws JsonProcessingException
     */
    public static void main(final String[] args) throws JsonProcessingException {
        final Rss rss = new Rss("http://rss.rssad.jp/rss/codezine/new/20/index.xml");
        rss.parse();
        System.out.println(new ObjectMapper().writeValueAsString(rss));
    }
}
