package jp.toastkid.libs.http;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import org.junit.Test;

/**
 * Response's test.
 * @author Toast kid
 *
 */
public class ResponseTest {

    /**
     * test {@link Response#equals(Object)}.
     * @throws UnsupportedEncodingException
     */
    @Test
    public final void testEquals() throws UnsupportedEncodingException {
        final Response response1 = makeObj();
        final Response response2 = makeObj();
        assertEquals(response1, response2);
    }

    /**
     * test {@link Response#hashCode()}.
     * @throws UnsupportedEncodingException
     */
    @Test
    public final void testHashCode() throws UnsupportedEncodingException {
        final Response response1 = makeObj();
        final Response response2 = makeObj();
        assertEquals(response1.hashCode(), response2.hashCode());
    }

    /**
     * test {@link Response#toString()}.
     * @throws UnsupportedEncodingException
     */
    @Test
    public final void testToString() throws UnsupportedEncodingException {
        final Response response = makeObj();
        assertTrue(response instanceof Response);
        assertEquals("{tomato=[fake]}", response.header().toString());
        assertEquals("[116, 111, 109, 97, 116, 111]", Arrays.toString(response.body()));
    }

    /**
     * check has body.
     * @throws UnsupportedEncodingException
     */
    @Test
    public final void testBody() throws UnsupportedEncodingException {
        assertTrue(Arrays.equals(new byte[]{116,111,109,97,116,111}, makeObj().body()));
    }

    /**
     * check get text.
     * @throws UnsupportedEncodingException
     */
    @Test
    public final void testText() throws UnsupportedEncodingException {
        assertEquals("tomato", makeObj().text());
    }

    /**
     * check get text optional.
     * @throws UnsupportedEncodingException
     */
    @Test
    public final void testTextOpt() throws UnsupportedEncodingException {
        final Optional<String> textOpt = makeObj().textOpt();
        assertTrue(textOpt instanceof Optional);
        textOpt.ifPresent(text -> { assertEquals("tomato", text);});
    }

    /**
     * check get text.
     * @throws UnsupportedEncodingException
     */
    @Test
    public final void testHeader() throws UnsupportedEncodingException {
        assertEquals("{tomato=[fake]}", makeObj().header().toString());
    }

    /**
     * make test instance.
     * @return test instance.
     * @throws UnsupportedEncodingException
     */
    private Response makeObj() throws UnsupportedEncodingException {
        final Response.Builder builder = new Response.Builder();
        builder.setBody("tomato".getBytes("UTF-8"));
        builder.setHeader(new HashMap<String, List<String>>() {
            /** serialVersionUID. */
            private static final long serialVersionUID = 1L;
            {
                put("tomato", Arrays.asList("fake"));
            }}
        );
        return builder.build();
    }

}
