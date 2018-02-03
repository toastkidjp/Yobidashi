/*
 * Copyright (c) 2017 toastkidjp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html.
 */
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
