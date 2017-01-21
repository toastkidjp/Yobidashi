package jp.toastkid.rss;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

/**
 * TODO write test.
 * @author Toast kid
 *
 */
public class Rss {

    public String title;
    public List<String> subjects;
    public String creator;
    public String date;
    public List<Item> items;

    public static class Item {
        public String title;
        public String link;
        public String date;
        public String description;
        public StringBuilder content;

        public Item() {
            this.content  = new StringBuilder();
        }
    }

    /**
     * initialize list.
     */
    public Rss() {
        this.subjects = new ArrayList<>();
        this.items    = new ArrayList<>();
    }

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
    public String url;

    public void setDescription(final String desc) {
        this.description = desc;
    }

    public String getDescription() {
        return description;
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

    public void setLink(final String link) {
        this.link = link;
    }

    public void addItem(final Item item) {
        this.items.add(item);
    }

    public List<Item> items() {
        return this.items;
    }
}
