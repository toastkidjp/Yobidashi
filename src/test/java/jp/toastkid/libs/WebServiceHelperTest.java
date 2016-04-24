package jp.toastkid.libs;

import static org.junit.Assert.*;
import jp.toastkid.libs.WebServiceHelper.Type;

import org.junit.Test;

/**
 * WebServiceHelper's Test
 * @author Toast kid
 *
 */
public class WebServiceHelperTest {

    /**
     * check {@link Type#getText()}.
     */
    @Test
    public final void testTypeGetText() {
        assertEquals("Y!辞書", Type.Y_DICTIONARY.getText());
        assertEquals("歌詞", Type.LYRICS.getText());
        assertEquals("Y!RTS", Type.REALTIME_SEARCH.getText());
        assertEquals("食べログ", Type.TABELOG.getText());
        assertEquals("Weblio", Type.WEBLIO.getText());
        assertEquals("Wikipe", Type.WIKIPEDIA.getText());
        assertEquals("Y!", Type.WEB_SEARCH.getText());
    }

    /**
     * check {@link Type#find(String)}.
     */
    @Test
    public final void testTypeFind() {
        assertEquals(Type.Y_DICTIONARY, Type.find("Y!辞書"));
        assertEquals(Type.LYRICS, Type.find("歌詞"));
        assertEquals(Type.REALTIME_SEARCH, Type.find("Y!RTS"));
        assertEquals(Type.TABELOG, Type.find("食べログ"));
        assertEquals(Type.WEBLIO, Type.find("Weblio"));
        assertEquals(Type.WIKIPEDIA, Type.find("Wikipe"));
        assertEquals(Type.WEB_SEARCH, Type.find("Y!"));
        // null case.
        assertEquals(Type.WEB_SEARCH, Type.find(null));
        assertEquals(Type.WEB_SEARCH, Type.find("Google"));
    }

    /**
     * check buildRequestUrl.
     */
    @Test
    public final void testBuildRequestUrl() {
        final String expected = "http://search.yahoo.co.jp/search?p=%E3%83%88%E3%83%9E%E3%83%88"
                + "&search.x=1&fr=top_ga1_sa&tid=top_ga1_sa&ei=UTF-8&aq=&oq=";
        assertEquals(expected, WebServiceHelper.buildRequestUrl("トマト", Type.WEB_SEARCH));
        assertEquals(expected, WebServiceHelper.buildRequestUrl("トマト", "Y!"));
        assertEquals(expected, WebServiceHelper.buildRequestUrl("トマト", (String) null));
    }

}
