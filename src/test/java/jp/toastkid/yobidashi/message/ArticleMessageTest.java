package jp.toastkid.yobidashi.message;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import jp.toastkid.yobidashi.message.ArticleMessage.Command;

/**
 * {@link ArticleMessage}'s test cases.
 *
 * @author Toast kid
 *
 */
public class ArticleMessageTest {

    /**
     * Check {@link ArticleMessage#makeLength()}.
     */
    @Test
    public void testMakeLength() {
        final ArticleMessage length = ArticleMessage.makeLength();
        assertEquals(Command.LENGTH, length.getCommand());
    }

    /**
     * Check {@link ArticleMessage#makeSlideShow()}.
     */
    @Test
    public void testMakeSlideShow() {
        final ArticleMessage slideShow = ArticleMessage.makeSlideShow();
        assertEquals(Command.SLIDE_SHOW, slideShow.getCommand());
    }

    /**
     * Check {@link ArticleMessage#makeSearch}.
     */
    @Test
    public void testMakeSearch() {
        final ArticleMessage search = ArticleMessage.makeSearch();
        assertEquals(Command.SEARCH, search.getCommand());
    }

    /**
     * Check {@link ArticleMessage#makeNew()}.
     */
    @Test
    public void testMakeNew() {
        final ArticleMessage make = ArticleMessage.makeNew();
        assertEquals(Command.MAKE, make.getCommand());
    }

    /**
     * Check {@link ArticleMessage#makeCopy()}.
     */
    @Test
    public void testMakeCopy() {
        final ArticleMessage copy = ArticleMessage.makeCopy();
        assertEquals(Command.COPY, copy.getCommand());
    }

    /**
     * Check {@link ArticleMessage#makeRename()}.
     */
    @Test
    public void testMakeRename() {
        final ArticleMessage rename = ArticleMessage.makeRename();
        assertEquals(Command.RENAME, rename.getCommand());
    }

    /**
     * Check {@link ArticleMessage#makeDelete()}.
     */
    @Test
    public void testMakeDelete() {
        final ArticleMessage delete = ArticleMessage.makeDelete();
        assertEquals(Command.DELETE, delete.getCommand());
    }

    /**
     * Check {@link ArticleMessage#makeConvertAobun()}.
     */
    @Test
    public void testMakeConvertAobun() {
        final ArticleMessage convertAobun = ArticleMessage.makeConvertAobun();
        assertEquals(Command.CONVERT_AOBUN, convertAobun.getCommand());
    }

    /**
     * Check {@link ArticleMessage#makeConvertEpub()}.
     */
    @Test
    public void testMakeConvertEpub() {
        final ArticleMessage convertEpub = ArticleMessage.makeConvertEpub();
        assertEquals(Command.CONVERT_EPUB, convertEpub.getCommand());
    }

    /**
     * Check {@link ArticleMessage#makeWordCloud()}.
     */
    @Test
    public void testMakeWordCloud() {
        final ArticleMessage wordCloud = ArticleMessage.makeWordCloud();
        assertEquals(Command.WORD_CLOUD, wordCloud.getCommand());
    }

}
