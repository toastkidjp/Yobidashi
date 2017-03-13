package jp.toastkid.libs.temperature;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import jp.toastkid.libs.temperature.TemperatureConverter;

/**
 * {@link TemperatureConverter} test case.
 *
 * @author Toast kid
 *
 */
public class TemperatureConverterTest {

    /**
     * Test of {@link TemperatureConverter#fToC(double)}.
     */
    @Test
    public void test_fToC() {
        assertEquals(36.8d, TemperatureConverter.fToC(98.24d), 0.1d);;
    }

    /**
     * Test of {@link TemperatureConverter#cToF(double)}.
     */
    @Test
    public void test_cToF() {
        assertEquals(98.24d, TemperatureConverter.cToF(36.8d), 0.1d);
    }

}
