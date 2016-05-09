package com.github.petr_s.nmea.core;

import org.hamcrest.Description;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatcher;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Matchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@RunWith(MockitoJUnitRunner.class)
public class NMEAParserTest {
    @Spy
    NMEAHandler handler = new NMEAAdapter();

    @Test(expected = NullPointerException.class)
    public void testConstructorNull() throws Exception {
        new NMEAParser(null);
    }

    @Test(expected = NullPointerException.class)
    public void testParseNullSentence() throws Exception {
        new NMEAParser(handler).parse(null);
    }

    @Test
    public void testParseEmpty() throws Exception {
        new NMEAParser(handler).parse("");

        verify(handler).onStart();
        verify(handler).onUnrecognized("");
        verify(handler).onFinished();
        verifyNoMoreInteractions(handler);
    }

    @Test
    public void testParseGPRMC() throws Exception {
        String sentence = "$GPRMC,163407.000,A,5004.7485,N,01423.8956,E,0.04,36.97,180416,,*38";
        new NMEAParser(handler).parse(sentence);

        verify(handler).onStart();
        verify(handler).onRMC(eq(1460937600000L),
                eq(59647000L),
                doubleThat(new RoughlyEqDouble(52.14583)),
                doubleThat(new RoughlyEqDouble(16.87111)),
                floatThat(new RoughlyEqFloat(0.02057f)),
                floatThat(new RoughlyEqFloat(36.97f)));
        verify(handler).onFinished();
        verifyNoMoreInteractions(handler);
    }

    @Test
    public void testParseGPRMCBadChecksum() throws Exception {
        String sentence = "$GPRMC,163407.000,A,5004.7485,N,01423.8956,E,0.04,36.97,180416,,*42";
        new NMEAParser(handler).parse(sentence);

        verify(handler).onStart();
        verify(handler).onBadChecksum(66, 56);
        verify(handler).onFinished();
        verifyNoMoreInteractions(handler);
    }

    @Test
    public void testParseGPRMCEOL() throws Exception {
        String sentence = "\n$GPRMC,163407.000,A,5004.7485,N,01423.8956,E,0.04,36.97,180416,,*42";
        new NMEAParser(handler).parse(sentence);

        verify(handler).onStart();
        verify(handler).onUnrecognized(sentence);
        verify(handler).onFinished();
        verifyNoMoreInteractions(handler);
    }

    @Test
    public void testParseGPGGA() throws Exception {
        String sentence = "$GPGGA,163407.000,5004.7485,N,01423.8956,E,1,07,1.7,285.7,M,45.5,M,,0000*5F";
        new NMEAParser(handler).parse(sentence);

        verify(handler).onStart();
        verify(handler).onGGA(eq(59647000L),
                doubleThat(new RoughlyEqDouble(52.14583)),
                doubleThat(new RoughlyEqDouble(16.87111)),
                floatThat(new RoughlyEqFloat(240.2f)),
                eq(NMEAHandler.FixQuality.GPS),
                eq(7),
                floatThat(new RoughlyEqFloat(1.7f)));
        verify(handler).onFinished();
        verifyNoMoreInteractions(handler);
    }

    private class RoughlyEqDouble extends ArgumentMatcher<Double> {
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

    private class RoughlyEqFloat extends ArgumentMatcher<Float> {
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