package jp.toastkid.libs.tinysegmenter;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * {@link CharacterClassifier}'s test cases.
 * @author Toast kid
 *
 */
public class CharacterClassifierTest {

    /**
     * check {@link CharacterClassifier#classify(String)}.
     */
    @Test
    public final void testClassify() {
        final CharacterClassifier c = new CharacterClassifier();
        assertEquals("N", c.classify("1"));
        assertEquals("N", c.classify("１"));
        assertEquals("I", c.classify("いち"));
        assertEquals("K", c.classify("イチ"));
        assertEquals("K", c.classify("ｲﾁ"));
        assertEquals("H", c.classify("壱"));
        assertEquals("H", c.classify("〆"));
        assertEquals("A", c.classify("a"));
        assertEquals("A", c.classify("A"));
        assertEquals("A", c.classify("ａ"));
        assertEquals("A", c.classify("Ａ"));
    }

}
