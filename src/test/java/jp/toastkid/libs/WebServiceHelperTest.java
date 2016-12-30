package jp.toastkid.libs;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import jp.toastkid.libs.WebServiceHelper.Type;

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
        assertEquals("Dictionary", Type.Y_DICTIONARY.getText());
        assertEquals("Realtime", Type.REALTIME_SEARCH.getText());
        assertEquals("Restaurant", Type.TABELOG.getText());
        assertEquals("Weblio", Type.WEBLIO.getText());
        assertEquals("Wikipedia", Type.WIKIPEDIA.getText());
        assertEquals("Y!", Type.WEB_SEARCH.getText());
    }

    /**
     * check {@link Type#find(String)}.
     */
    @Test
    public final void testTypeFind() {
        assertEquals(Type.Y_DICTIONARY, Type.find("Dictionary"));
        assertEquals(Type.REALTIME_SEARCH, Type.find("Realtime"));
        assertEquals(Type.TABELOG, Type.find("Restaurant"));
        assertEquals(Type.WEBLIO, Type.find("Weblio"));
        assertEquals(Type.WIKIPEDIA, Type.find("Wikipedia"));
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
        final String expected = "https://search.yahoo.co.jp/search?p=%E3%83%88%E3%83%9E%E3%83%88"
                + "&search.x=1&ei=UTF-8&aq=&oq=";
        assertEquals(expected, WebServiceHelper.buildRequestUrl("トマト", Type.WEB_SEARCH));
        assertEquals(expected, WebServiceHelper.buildRequestUrl("トマト", "Y!"));
        assertEquals(expected, WebServiceHelper.buildRequestUrl("トマト", (String) null));
    }

}
