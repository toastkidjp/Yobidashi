/*
 * Copyright (c) 2017 toastkidjp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package jp.toastkid.rss;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

/**
 * RSS item.
 * @author Toast kid
 *
 */
public class Rss {

    /** Empty object. */
    private static final Rss EMPTY = new Rss();

    /** RSS' title. */
    private String title;

    /** RSS' subjects. */
    private final List<String> subjects;

    /** RSS' creator. */
    private String creator;

    /** RSS' date. */
    private String date;

    /** RSS' items. */
    private final List<Item> items;

    /** RSS' Link. */
    private String link;

    /** RSS' URL. */
    private String url;

    /** RSS' description. */
    private String description;

    /** RSS' item . */
    public static class Item {
        private String title;
        private String link;
        private String date;
        private String description;
        private final StringBuilder content;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getLink() {
            return link;
        }

        public void setLink(String link) {
            this.link = link;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public void addContent(final String str) {
            content.append(str);
        }

        public String getContent() {
            return content.toString();
        }

        public Item() {
            this.content  = new StringBuilder();
        }
    }

    /**
     * Initialize lists.
     */
    public Rss() {
        this.subjects = new ArrayList<>();
        this.items    = new ArrayList<>();
    }

    /**
     * Return title.
     * @return title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Return link.
     * @return
     */
    public String getLink() {
        return link;
    }

    /**
     * Set passed Description.
     * @param desc
     */
    public void setDescription(final String desc) {
        this.description = desc;
    }

    /**
     * Return description.
     * @return
     */
    public String getDescription() {
        return description;
    }

    /**
     * Return expanded title.
     * @return
     */
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
     * Set link.
     * @param link
     */
    public void setLink(final String link) {
        this.link = link;
    }

    /**
     * Add passed item.
     * @param item
     */
    public void addItem(final Item item) {
        this.items.add(item);
    }

    /**
     * Return this items.
     * @return
     */
    public List<Item> items() {
        return this.items;
    }

    /**
     * Getter of creator.
     * @return
     */
    public String getCreator() {
        return creator;
    }

    /**
     * Setter of creator,
     *
     * @param creator
     */
    public void setCreator(String creator) {
        this.creator = creator;
    }

    /**
     * Getter of date.
     * @return
     */
    public String getDate() {
        return date;
    }

    /**
     * Setter of Date.
     * @return
     */
    public void setDate(String date) {
        this.date = date;
    }

    /**
     * Getter of URL.
     * @return
     */
    public String getUrl() {
        return url;
    }

    /**
     * Setter of URL.
     * @return
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * Add passed subject to subjects.
     * @return
     */
    public void addSubjects(String subject) {
        this.subjects.add(subject);
    }

    /**
     * Getter of subjects.
     * @return
     */
    public List<String> getSubjects() {
        return subjects;
    }

    /**
     * Setter of title.
     * @return
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Return empty object.
     * @return
     */
    public static Rss empty() {
        return EMPTY;
    }

}
