package com.github.petr_s.nmea;

import org.hamcrest.Description;
import org.mockito.ArgumentMatcher;

public class Helper {
    public static ArgumentMatcher<Double> roughlyEq(final double expected) {
        return roughlyEq(expected, 0.0001);
    }

    public static ArgumentMatcher<Double> roughlyEq(final double expected, final double delta) {
        return new ArgumentMatcher<Double>() {
            @Override
            public boolean matches(Object argument) {
                return Math.abs(expected - (Double) argument) <= delta;
            }

            @Override
            public void describeTo(Description description) {
                description.appendText(Double.toString(expected) + "±" + Double.toString(delta));
            }
        };
    }

    public static ArgumentMatcher<Float> roughlyEq(final float expected) {
        return roughlyEq(expected, 0.0001f);
    }

    public static ArgumentMatcher<Float> roughlyEq(final float expected, final float delta) {
        return new ArgumentMatcher<Float>() {
            @Override
            public boolean matches(Object argument) {
                return Math.abs(expected - (Float) argument) <= delta;
            }

            @Override
            public void describeTo(Description description) {
                description.appendText(Float.toString(expected) + "±" + Float.toString(delta));
            }
        };
    }
}
