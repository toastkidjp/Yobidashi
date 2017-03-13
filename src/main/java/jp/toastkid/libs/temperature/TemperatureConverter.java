package jp.toastkid.libs.temperature;

/**
 * Fahrenheit and Celsius temperature converter.
 *
 * @author Toast kid
 */
public class TemperatureConverter {

    /**
     * Fahrenheit temperature convert to Celsius temperature.
     * @param fahrenheit Fahrenheit temperature
     * @return Celsius temperature
     */
    public static double fToC(final double fahrenheit) {
        return (fahrenheit - 32.0d) / 1.8d;
    }

    /**
     * Celsius temperature convert to Fahrenheit temperature.
     * @param celsius Celsius temperature
     * @return Fahrenheit temperature
     */
    public static double cToF(final double celsius) {
        return (celsius * 1.8d) + 32.0d;
    }
}
