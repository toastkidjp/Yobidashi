package jp.toastkid.yobidashi;

import static org.junit.Assert.assertEquals;

import java.nio.file.Paths;

import org.junit.Test;

import jp.toastkid.libs.utils.FileUtil;

/**
 * For test coverage.
 *
 * @author Toast kid
 *
 */
public class DefinesTest {

    /**
     * {@link Defines#findInstallDir()}.
     */
    @Test
    public void testFindInstallDir() {
        assertEquals(
                FileUtil.FILE_PROTOCOL
                    + Paths.get(".").toAbsolutePath().getParent().toString().replace("\\", "/") + "/",
                Defines.findInstallDir()
                );
    }

}
