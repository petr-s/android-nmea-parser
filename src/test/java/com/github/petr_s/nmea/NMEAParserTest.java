package com.github.petr_s.nmea;

import android.location.Location;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;


@RunWith(MockitoJUnitRunner.class)
public class NMEAParserTest {
    @Spy
    NMEAHandler handler = new NMEAAdapter();

    @Test(expected = NullPointerException.class)
    public void testConstructorNull() throws Exception {
        new NMEAParser(null);
    }

    @Ignore
    @Test
    public void testParseLocation() throws Exception {
        NMEAParser parser = new NMEAParser(handler);
        parser.parse("$GPRMC,163407.000,A,5004.7485,N,01423.8956,E,0.04,36.97,180416,,*38");
        parser.parse("$GPGGA,163407.000,5004.7485,N,01423.8956,E,1,07,1.7,285.7,M,45.5,M,,0000*5F");

        verify(handler, times(2)).onStart();
        verify(handler, times(2)).onFinish();
        verify(handler).onLocation(any(Location.class));
        verifyNoMoreInteractions(handler);
    }
}