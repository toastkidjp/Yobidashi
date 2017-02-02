package jp.toastkid.libs.utils;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * {@link RuntimeUtil}'s test cases.
 *
 * @author Toast kid
 *
 */
public class RuntimeUtilTest {

    /**
     * Test methods.
     */
    @Test
    public void test() {
        RuntimeUtil.callCalculator().destroy();
        RuntimeUtil.callCmd().destroy();
        RuntimeUtil.callExplorer(".").destroy();
    }

    /**
     * Test {@link RuntimeUtil#calcUsedMemorySize()}.
     */
    @Test
    public final void testCalcUsedMemorySize() {
        assertTrue(0 != RuntimeUtil.calcUsedMemorySize());
    }

}
