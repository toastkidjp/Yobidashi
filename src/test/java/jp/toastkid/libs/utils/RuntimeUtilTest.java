package jp.toastkid.libs.utils;

import static org.junit.Assert.assertTrue;
import jp.toastkid.libs.utils.RuntimeUtil;

import org.junit.Test;

/**
 * {@link RuntimeUtil}'s test cases.
 * @author Toast kid
 *
 */
public class RuntimeUtilTest {

    /**
     * test {@link RuntimeUtil#calcUsedMemorySize()}.
     */
    @Test
    public final void testCalcUsedMemorySize() {
        assertTrue(0 != RuntimeUtil.calcUsedMemorySize());
    }

}
