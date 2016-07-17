package jp.toastkid.gui.jfx.wordcloud;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.mockito.internal.util.reflection.Whitebox;

/**
 * {@link FxWordCloud}'s test.
 *
 * @author Toast kid
 *
 */
public class FxWordCloudTest {

    /**
     * check using default value when called default Builder#build().
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     * @throws NoSuchFieldException
     * @throws SecurityException
     */
    @Test
    public void checkUsingDefault() throws IllegalArgumentException, IllegalAccessException,
                              NoSuchFieldException, SecurityException {
        final FxWordCloud fxwc = new FxWordCloud.Builder().build();
        assertEquals(FxWordCloud.NUMBER_OF_WORDS, Whitebox.getInternalState(fxwc, "numOfWords"));
        assertEquals(FxWordCloud.MIN_FONT_SIZE,   Whitebox.getInternalState(fxwc, "minFontSize"));
        assertEquals(FxWordCloud.MAX_FONT_SIZE,   Whitebox.getInternalState(fxwc, "maxFontSize"));
    }

    /**
     * check using default value when called default Builder#build().
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     * @throws NoSuchFieldException
     * @throws SecurityException
     */
    @Test
    public void checkUsingParameter() throws IllegalArgumentException, IllegalAccessException,
                              NoSuchFieldException, SecurityException {
        final FxWordCloud fxwc = new FxWordCloud.Builder()
                .setNumOfWords(20)
                .setMinFontSize(10.0)
                .setMaxFontSize(100.0)
                .build();
        assertEquals(20, Whitebox.getInternalState(fxwc, "numOfWords"));
        assertEquals(10.0,   Whitebox.getInternalState(fxwc, "minFontSize"));
        assertEquals(100.0,   Whitebox.getInternalState(fxwc, "maxFontSize"));
    }

    /**
     * check using default value when called default Builder#build().
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     * @throws NoSuchFieldException
     * @throws SecurityException
     */
    @Test
    public void checkReplaceParameter() throws IllegalArgumentException, IllegalAccessException,
                              NoSuchFieldException, SecurityException {
        final FxWordCloud fxwc = new FxWordCloud.Builder()
                .setNumOfWords(-20)
                .setMinFontSize(-10.0)
                .setMaxFontSize(-100.0)
                .build();
        assertEquals(FxWordCloud.NUMBER_OF_WORDS, Whitebox.getInternalState(fxwc, "numOfWords"));
        assertEquals(FxWordCloud.MIN_FONT_SIZE,   Whitebox.getInternalState(fxwc, "minFontSize"));
        assertEquals(FxWordCloud.MAX_FONT_SIZE,   Whitebox.getInternalState(fxwc, "maxFontSize"));
    }
}
