package jp.toastkid.libs.tinysegmenter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import org.junit.Before;
import org.junit.Test;

/**
 * {@link TinySegmenter}'s test cases
 * @author Toast kid
 *
 */
public class TinySegmenterTest {

    /** instance. */
    private TinySegmenter ts;

    /**
     * initialize instance.
     */
    @Before
    public void setup(){
        this.ts = TinySegmenter.getInstance();
    }

    /**
     * check {@link TinySegmenter}'s constructor.
     */
    @Test
    public final void testConstructor() {
        assertSame(TinySegmenter.getInstance(), TinySegmenter.getInstance());
    }

    /**
     * check {@link TinySegmenter#segment(String)}.
     */
    @Test
    public final void testSegment() {
        assertEquals(
                "[隣, の, 客, は, AK, 4, 7, 振り, かざし, て, ギャアギャア, わめき, た, てる, 客, だ]",
                ts.segment("隣の客はAK47振りかざしてギャアギャアわめきたてる客だ。").toString()
                );
    }
}
