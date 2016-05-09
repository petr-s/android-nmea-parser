package com.github.petr_s.nmea;

import org.hamcrest.Description;
import org.mockito.ArgumentMatcher;

public class Helper {
    public static class RoughlyEqDouble extends ArgumentMatcher<Double> {
        private static final double DELTA = 0.0001;
        private double expected;

        public RoughlyEqDouble(double expected) {
            this.expected = expected;
        }

        @Override
        public boolean matches(Object argument) {
            return Math.abs(expected - (Double) argument) <= DELTA;
        }

        @Override
        public void describeTo(Description description) {
            description.appendText(Double.toString(expected) + "±" + Double.toString(DELTA));
        }
    }

    public static class RoughlyEqFloat extends ArgumentMatcher<Float> {
        private static final float DELTA = 0.0001f;
        private float expected;

        public RoughlyEqFloat(float expected) {
            this.expected = expected;
        }

        @Override
        public boolean matches(Object argument) {
            return Math.abs(expected - (Float) argument) <= DELTA;
        }

        @Override
        public void describeTo(Description description) {
            description.appendText(Float.toString(expected) + "±" + Float.toString(DELTA));
        }
    }
}
