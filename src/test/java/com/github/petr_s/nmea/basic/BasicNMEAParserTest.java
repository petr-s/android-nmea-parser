package com.github.petr_s.nmea.basic;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.HashSet;

import static com.github.petr_s.nmea.Helper.eq;
import static com.github.petr_s.nmea.Helper.roughlyEq;
import static com.github.petr_s.nmea.basic.BasicNMEAHandler.FixType.Fix3D;
import static org.mockito.Matchers.doubleThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.floatThat;
import static org.mockito.Mockito.argThat;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class BasicNMEAParserTest {
    @Spy
    BasicNMEAHandler handler = new BasicNMEAAdapter();

    @Test(expected = NullPointerException.class)
    public void testConstructorNull() throws Exception {
        new BasicNMEAParser(null);
    }

    @Test(expected = NullPointerException.class)
    public void testParseNullSentence() throws Exception {
        new BasicNMEAParser(handler).parse(null);
    }

    @Test
    public void testParseEmpty() throws Exception {
        new BasicNMEAParser(handler).parse("");

        verify(handler).onStart();
        verify(handler).onUnrecognized("");
        verify(handler).onFinished();
        verifyNoMoreInteractions(handler);
    }

    @Test
    public void testParseGPRMC() throws Exception {
        String sentence = "$GPRMC,163407.000,A,5004.7485,N,01423.8956,E,0.04,36.97,180416,,*38";
        new BasicNMEAParser(handler).parse(sentence);

        verify(handler).onStart();
        verify(handler).onRMC(eq(1460937600000L),
                eq(59647000L),
                doubleThat(roughlyEq(50.07914)),
                doubleThat(roughlyEq(14.39825)),
                floatThat(roughlyEq(0.02057f)),
                floatThat(roughlyEq(36.97f)));
        verify(handler).onFinished();
        verifyNoMoreInteractions(handler);
    }

    @Test
    public void testParseGPRMC_2_3() throws Exception {
        String sentence = "$GPRMC,093933.40,A,5004.52493,N,01424.28771,E,0.277,,130616,,,A*76";
        new BasicNMEAParser(handler).parse(sentence);

        verify(handler).onStart();
        verify(handler).onRMC(eq(1465776000000L),
                eq(34773400L),
                doubleThat(roughlyEq(50.075415)),
                doubleThat(roughlyEq(14.404795)),
                floatThat(roughlyEq(0.142501f)),
                floatThat(roughlyEq(0.0f)));
        verify(handler).onFinished();
        verifyNoMoreInteractions(handler);
    }

    @Test
    public void testParseGPRMCBadChecksum() throws Exception {
        String sentence = "$GPRMC,163407.000,A,5004.7485,N,01423.8956,E,0.04,36.97,180416,,*42";
        new BasicNMEAParser(handler).parse(sentence);

        verify(handler).onStart();
        verify(handler).onBadChecksum(66, 56);
        verify(handler).onFinished();
        verifyNoMoreInteractions(handler);
    }

    @Test
    public void testParseGPRMCEOL() throws Exception {
        String sentence = "\n$GPRMC,163407.000,A,5004.7485,N,01423.8956,E,0.04,36.97,180416,,*42";
        new BasicNMEAParser(handler).parse(sentence);

        verify(handler).onStart();
        verify(handler).onUnrecognized(sentence);
        verify(handler).onFinished();
        verifyNoMoreInteractions(handler);
    }

    @Test
    public void testParseGPGGA() throws Exception {
        String sentence = "$GPGGA,163407.000,5004.7485,N,01423.8956,E,1,07,1.7,285.7,M,45.5,M,,0000*5F";
        new BasicNMEAParser(handler).parse(sentence);

        verify(handler).onStart();
        verify(handler).onGGA(eq(59647000L),
                doubleThat(roughlyEq(50.07914)),
                doubleThat(roughlyEq(14.39825)),
                floatThat(roughlyEq(240.2f)),
                eq(BasicNMEAHandler.FixQuality.GPS),
                eq(7),
                floatThat(roughlyEq(1.7f)));
        verify(handler).onFinished();
        verifyNoMoreInteractions(handler);
    }

    @Test
    public void testParseGPGGANegativeGeoid() throws Exception {
        String sentence = "$GPGGA,214213.00,3249.263664,N,11710.592247,W,1,11,0.6,102.2,M,-26.0,M,,*51";
        new BasicNMEAParser(handler).parse(sentence);

        verify(handler).onStart();
        verify(handler).onGGA(eq(78133000L),
                doubleThat(roughlyEq(32.82106)),
                doubleThat(roughlyEq(-117.17653)),
                floatThat(roughlyEq(128.2f)),
                eq(BasicNMEAHandler.FixQuality.GPS),
                eq(11),
                floatThat(roughlyEq(0.6f)));
        verify(handler).onFinished();
        verifyNoMoreInteractions(handler);
    }

    @Test
    public void testParseGPGSVSingle() throws Exception {
        String sentence = "$GPGSV,3,1,11,29,86,273,30,25,60,110,38,31,52,278,47,02,28,050,39*7D";
        new BasicNMEAParser(handler).parse(sentence);

        verify(handler).onStart();
        verify(handler).onGSV(eq(11),
                eq(0),
                eq(29),
                eq(86.0f),
                eq(273.0f),
                eq(30));
        verify(handler).onGSV(eq(11),
                eq(1),
                eq(25),
                eq(60.0f),
                eq(110.0f),
                eq(38));
        verify(handler).onGSV(eq(11),
                eq(2),
                eq(31),
                eq(52.0f),
                eq(278.0f),
                eq(47));
        verify(handler).onGSV(eq(11),
                eq(3),
                eq(2),
                eq(28.0f),
                eq(50.0f),
                eq(39));
        verify(handler).onFinished();
        verifyNoMoreInteractions(handler);
    }

    @Test
    public void testParseGPGSVFull() throws Exception {
        BasicNMEAParser parser = new BasicNMEAParser(handler);
        parser.parse("$GPGSV,3,1,11,29,86,273,30,25,60,110,38,31,52,278,47,02,28,050,39*7D");
        parser.parse("$GPGSV,3,2,11,12,23,110,34,26,18,295,29,21,17,190,30,05,11,092,25*72");
        parser.parse("$GPGSV,3,3,11,14,02,232,13,23,02,346,12,20,01,135,13*48");

        verify(handler, times(3)).onStart();
        verify(handler).onGSV(eq(11),
                eq(0),
                eq(29),
                eq(86.0f),
                eq(273.0f),
                eq(30));
        verify(handler).onGSV(eq(11),
                eq(1),
                eq(25),
                eq(60.0f),
                eq(110.0f),
                eq(38));
        verify(handler).onGSV(eq(11),
                eq(2),
                eq(31),
                eq(52.0f),
                eq(278.0f),
                eq(47));
        verify(handler).onGSV(eq(11),
                eq(3),
                eq(2),
                eq(28.0f),
                eq(50.0f),
                eq(39));
        verify(handler).onGSV(eq(11),
                eq(4),
                eq(12),
                eq(23.0f),
                eq(110.0f),
                eq(34));
        verify(handler).onGSV(eq(11),
                eq(5),
                eq(26),
                eq(18.0f),
                eq(295.0f),
                eq(29));
        verify(handler).onGSV(eq(11),
                eq(6),
                eq(21),
                eq(17.0f),
                eq(190.0f),
                eq(30));
        verify(handler).onGSV(eq(11),
                eq(7),
                eq(5),
                eq(11.0f),
                eq(92.0f),
                eq(25));
        verify(handler).onGSV(eq(11),
                eq(8),
                eq(14),
                eq(2.0f),
                eq(232.0f),
                eq(13));
        verify(handler).onGSV(eq(11),
                eq(9),
                eq(23),
                eq(2.0f),
                eq(346.0f),
                eq(12));
        verify(handler).onGSV(eq(11),
                eq(10),
                eq(20),
                eq(1.0f),
                eq(135.0f),
                eq(13));
        verify(handler, times(3)).onFinished();
        verifyNoMoreInteractions(handler);
    }

    @Test
    public void testParseGPGSA() throws Exception {
        String sentence = "$GPGSA,A,3,25,02,26,05,29,31,21,12,,,,,1.6,1.0,1.3*3B";
        new BasicNMEAParser(handler).parse(sentence);

        verify(handler).onStart();
        verify(handler).onGSA(eq(Fix3D),
                argThat(eq(new HashSet<>(Arrays.asList(new Integer[]{2, 5, 21, 25, 26, 12, 29, 31})))),
                floatThat(roughlyEq(1.6f)),
                floatThat(roughlyEq(1.0f)),
                floatThat(roughlyEq(1.3f)));
        verify(handler).onFinished();
        verifyNoMoreInteractions(handler);
    }
}