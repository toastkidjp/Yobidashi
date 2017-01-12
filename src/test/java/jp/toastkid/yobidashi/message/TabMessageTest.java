package jp.toastkid.yobidashi.message;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import jp.toastkid.yobidashi.message.TabMessage.Command;

/**
 * {@link TabMessage}'s test case.
 *
 * @author Toast kid
 *
 */
public class TabMessageTest {

    /**
     * Check {@link TabMessage#makeClose()}.
     */
    @Test
    public void test_makeClose() {
        final TabMessage close = TabMessage.makeClose();
        assertEquals(Command.CLOSE, close.getCommand());
    }

    /**
     * Check {@link TabMessage#makeCloseAll()}.
     */
    @Test
    public void test_makeCloseAll() {
        final TabMessage closeAll = TabMessage.makeCloseAll();
        assertEquals(Command.CLOSE_ALL, closeAll.getCommand());
    }

    /**
     * Check {@link TabMessage#makeEdit()}.
     */
    @Test
    public void test_makeEdit() {
        final TabMessage edit = TabMessage.makeEdit();
        assertEquals(Command.EDIT, edit.getCommand());
    }

    /**
     * Check {@link TabMessage#makeOpen()}.
     */
    @Test
    public void test_makeOpen() {
        final TabMessage open = TabMessage.makeOpen();
        assertEquals(Command.OPEN, open.getCommand());
    }

    /**
     * Check {@link TabMessage#makePreview()}.
     */
    @Test
    public void test_makePreview() {
        final TabMessage preview = TabMessage.makePreview();
        assertEquals(Command.PREVIEW, preview.getCommand());
    }

    /**
     * Check {@link TabMessage#makeReload()}.
     */
    @Test
    public void test_makeReload() {
        final TabMessage reload = TabMessage.makeReload();
        assertEquals(Command.RELOAD, reload.getCommand());
    }

    /**
     * Check {@link TabMessage#makeSave()}.
     */
    @Test
    public void test_makeSave() {
        final TabMessage save = TabMessage.makeSave();
        assertEquals(Command.SAVE, save.getCommand());
    }

}
