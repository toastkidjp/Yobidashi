package jp.toastkid.article;

import static org.junit.Assert.assertEquals;
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
     * Check {@link ImageChooser#choose()}.
     */
    @Test
    public void test() {
        final String choose = new ImageChooser("src/test/resources/image/chooser/").choose();
        assertNotNull(choose);
        assertTrue(FileUtil.isImageFile(choose));
    }

    /**
     * Check irregular case.
     */
    @Test
    public void failCase() {
        assertEquals("", new ImageChooser("").choose());
    }

}
