package jp.toastkid.libs;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.apache.commons.lang3.StringUtils;


/**
 * 外部 Web サービス.
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
        Y_DICTIONARY("Y!辞書"), WEBLIO("Weblio"), TABELOG("食べログ"), WIKIPEDIA("Wikipe"),
        REALTIME_SEARCH("Y!RTS"), WEB_SEARCH("Y!"), LYRICS("歌詞");

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
     * @param type  検索種別(文字列)
     * @return 各種キーワード検索用の URL
     */
    public static String buildRequestUrl(
            final String query,
            final String type
            ) {
        return buildRequestUrl(query, Type.find(type));
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
        String targetQuery = null;
        try {
            targetQuery = URLEncoder.encode(query, "UTF-8");
        } catch (final UnsupportedEncodingException e1) {
            e1.printStackTrace();
        }

        switch (type) {
            case REALTIME_SEARCH:
                return "http://realtime.search.yahoo.co.jp/search?fr=top_ga1_sa&ei=utf-8&p=" + targetQuery;
            case WIKIPEDIA:
                return "http://ja.wikipedia.org/wiki/Special:Search?search="
                        + targetQuery + "&sourceid=Mozilla-search";
            case TABELOG:
                return "http://tabelog.com/japan/0/0/lst/?vs=1&sk="
                        + targetQuery + "&SrtT=trend&LstCosT=0&sa=&cid=top_navi1&sw=" + targetQuery;
            case WEBLIO:
                return "http://ejje.weblio.jp/content/" + targetQuery;
            case Y_DICTIONARY:
                return "http://dic.search.yahoo.co.jp/search?ei=UTF-8&fr=top_ga1_sa&p=" + targetQuery;
            case LYRICS:
                try {
                    targetQuery = URLEncoder.encode(query, "EUC-JP");
                } catch (final UnsupportedEncodingException e1) {
                   e1.printStackTrace();
                }
                return "http://music.goo.ne.jp/search/result.php?t=&n=" + targetQuery;
            case WEB_SEARCH:
            default:
                return "http://search.yahoo.co.jp/search?p=" + targetQuery
                        + "&search.x=1&fr=top_ga1_sa&tid=top_ga1_sa&ei=UTF-8&aq=&oq=";
        }
    }
}
