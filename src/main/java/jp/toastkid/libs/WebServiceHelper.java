/*
 * Copyright (c) 2017 toastkidjp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package jp.toastkid.libs;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.apache.commons.lang3.StringUtils;


/**
 * 外部 Web サービスのURLを生成する Helper.
 * @author Toast kid
 * @version 0.0.1
 */
public class WebServiceHelper {

    /**
     * 検索種別.
     * @author Toast kid
     *
     */
    public enum Type {
        Y_DICTIONARY("Dictionary"), WEBLIO("Weblio"), TABELOG("Restaurant"), WIKIPEDIA("Wikipedia"),
        Y_IMAGE("Image"), REALTIME_SEARCH("Realtime"), WEB_SEARCH("Y!"), Y_VIDEO("Video"),
        Y_MAP("Map"), Y_LOCO("Location");

        /** 検索種別の日本語名. */
        private final String text;

        /**
         * return text.
         * @return text
         */
        public String getText() {
            return text;
        }

        /**
         * 外部からのインスタンス生成を禁止する.
         */
        private Type(final String text) {
            this.text = text;
        }

        /**
         * 文字列から探す.
         * @param text
         * @return
         */
        public static Type find(final String text) {
            if (StringUtils.isBlank(text)) {
                return WEB_SEARCH;
            }
            for (final Type t : values()) {
                if (t.text.equals(text)) {
                    return t;
                }
            }
            return WEB_SEARCH;
        }
    }

    /**
     * 各種キーワード検索用の URL を返す。
     * @param targetQuery 検索キーワード
     * @param type  検索種別
     * @return 各種キーワード検索用の URL
     */
    public static String buildRequestUrl(
            final String query,
            final Type type
            ) {

        if (type == null) {
            return null;
        }

        String targetQuery = null;
        try {
            targetQuery = URLEncoder.encode(query, "UTF-8");
        } catch (final UnsupportedEncodingException e1) {
            e1.printStackTrace();
        }

        switch (type) {
            case REALTIME_SEARCH:
                return "http://realtime.search.yahoo.co.jp/search?ei=UTF-8&p=" + targetQuery;
            case WIKIPEDIA:
                return "https://ja.wikipedia.org/wiki/Special:Search?search=" + targetQuery;
            case TABELOG:
                return "https://tabelog.com/japan/0/0/lst/?vs=1&sk="
                        + targetQuery + "&SrtT=trend&LstCosT=0&sa=&cid=top_navi1&sw=" + targetQuery;
            case WEBLIO:
                return "http://ejje.weblio.jp/content/" + targetQuery;
            case Y_IMAGE:
                return "http://image.search.yahoo.co.jp/search?ei=UTF-8&p=" + targetQuery;
            case Y_DICTIONARY:
                return "http://dic.search.yahoo.co.jp/search?ei=UTF-8&p=" + targetQuery;
            case Y_VIDEO:
                return "http://video.search.yahoo.co.jp/search?ei=UTF-8&p=" + targetQuery;
            case Y_MAP:
                return "http://map.search.yahoo.co.jp/search?ei=UTF-8&p=" + targetQuery;
            case Y_LOCO:
                return "http://search.loco.yahoo.co.jp/search?ei=UTF-8&p=" + targetQuery;
            case WEB_SEARCH:
            default:
                return "https://search.yahoo.co.jp/search?p=" + targetQuery
                        + "&search.x=1&ei=UTF-8&aq=&oq=";
        }
    }
}
