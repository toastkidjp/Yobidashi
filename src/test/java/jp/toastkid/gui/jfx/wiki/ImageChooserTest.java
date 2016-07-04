package jp.toastkid.gui.jfx.wiki;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import jp.toastkid.libs.utils.FileUtil;

/**
 * {@link ImageChooser}'s test case.
 *
 * @author Toast kid
 *
 */
public class ImageChooserTest {

    /**
     * check {@link ImageChooser#choose()}.
     */
    @Test
    public void test() {
        final String choose = new ImageChooser("src/test/resources/image/chooser/").choose();
        assertNotNull(choose);
        assertTrue(FileUtil.isImageFile(choose));
    }

    /**
     * check irregular case.
     */
    @Test(expected=IndexOutOfBoundsException.class)
    public void failCase() {
        new ImageChooser("").choose();
    }

}
