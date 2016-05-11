package com.github.petr_s.nmea;

import android.location.Location;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import static com.github.petr_s.nmea.Helper.roughlyEq;
import static org.mockito.Mockito.*;


@RunWith(MockitoJUnitRunner.class)
public class NMEAParserTest {
    @Spy
    NMEAHandler handler = new NMEAAdapter();

    @Mock
    Location location;

    @Mock
    LocationFactory locationFactory;

    NMEAParser parser;

    @Before
    public void setUp() {
        parser = new NMEAParser(handler, locationFactory);
    }


    @Test(expected = NullPointerException.class)
    public void testConstructorNull() throws Exception {
        new NMEAParser(null);
    }

    @Test(expected = NullPointerException.class)
    public void testParseNull() throws Exception {
        parser.parse(null);
    }

    @Test
    public void testParseLocationRMCGGA() throws Exception {
        when(locationFactory.newLocation()).thenReturn(location);

        parser.parse("$GPRMC,163407.000,A,5004.7485,N,01423.8956,E,0.04,36.97,180416,,*38");
        parser.parse("$GPGGA,163407.000,5004.7485,N,01423.8956,E,1,07,1.7,285.7,M,45.5,M,,0000*5F");

        verify(handler, times(2)).onStart();
        verify(handler, times(2)).onFinish();
        verify(handler).onLocation(location);
        verifyNoMoreInteractions(handler);

        verify(location).setTime(eq(1460954639384L));
        verify(location).setLatitude(doubleThat(roughlyEq(52.14583)));
        verify(location).setLongitude(doubleThat(roughlyEq(16.87111)));
        verify(location).setAltitude(doubleThat(roughlyEq(240.2)));
        verify(location).setAccuracy(floatThat(roughlyEq(6.8f)));
        verify(location).setSpeed(floatThat(roughlyEq(0.02057f)));
        verify(location).setBearing(floatThat(roughlyEq(36.97f)));
        verifyNoMoreInteractions(location);

        verify(locationFactory).newLocation();
        verifyNoMoreInteractions(locationFactory);
    }

    @Test
    public void testParseLocationGGARMC() throws Exception {
        when(locationFactory.newLocation()).thenReturn(location);

        parser.parse("$GPGGA,163407.000,5004.7485,N,01423.8956,E,1,07,1.7,285.7,M,45.5,M,,0000*5F");
        parser.parse("$GPRMC,163407.000,A,5004.7485,N,01423.8956,E,0.04,36.97,180416,,*38");

        verify(handler, times(2)).onStart();
        verify(handler, times(2)).onFinish();
        verify(handler).onLocation(location);
        verifyNoMoreInteractions(handler);

        verify(location).setTime(eq(1460954639384L));
        verify(location).setLatitude(doubleThat(roughlyEq(52.14583)));
        verify(location).setLongitude(doubleThat(roughlyEq(16.87111)));
        verify(location).setAltitude(doubleThat(roughlyEq(240.2)));
        verify(location).setAccuracy(floatThat(roughlyEq(6.8f)));
        verify(location).setSpeed(floatThat(roughlyEq(0.02057f)));
        verify(location).setBearing(floatThat(roughlyEq(36.97f)));
        verifyNoMoreInteractions(location);

        verify(locationFactory).newLocation();
        verifyNoMoreInteractions(locationFactory);
    }

    @Test
    public void testParseLocationRMCGGADiffTime() throws Exception {
        when(locationFactory.newLocation()).thenReturn(location);

        parser.parse("$GPRMC,163407.000,A,5004.7485,N,01423.8956,E,0.04,36.97,180416,,*38");
        parser.parse("$GPGGA,163408.000,5004.7485,N,01423.8956,E,1,07,1.7,285.7,M,45.5,M,,0000*50");

        verify(handler, times(2)).onStart();
        verify(handler, times(2)).onFinish();
        verifyNoMoreInteractions(handler);

        verify(locationFactory, times(2)).newLocation();
        verifyNoMoreInteractions(locationFactory);
    }

    @Test
    public void testParseLocationGGARMCDiffTime() throws Exception {
        when(locationFactory.newLocation()).thenReturn(location);

        parser.parse("$GPGGA,163408.000,5004.7485,N,01423.8956,E,1,07,1.7,285.7,M,45.5,M,,0000*50");
        parser.parse("$GPRMC,163407.000,A,5004.7485,N,01423.8956,E,0.04,36.97,180416,,*38");

        verify(handler, times(2)).onStart();
        verify(handler, times(2)).onFinish();
        verifyNoMoreInteractions(handler);

        verify(locationFactory, times(2)).newLocation();
        verifyNoMoreInteractions(locationFactory);
    }

    @Test
    public void testParseLocationGGAGGA() throws Exception {
        when(locationFactory.newLocation()).thenReturn(location);

        parser.parse("$GPGGA,163407.000,5004.7485,N,01423.8956,E,1,07,1.7,285.7,M,45.5,M,,0000*5F");
        parser.parse("$GPGGA,163407.000,5004.7485,N,01423.8956,E,1,07,1.7,285.7,M,45.5,M,,0000*5F");

        verify(handler, times(2)).onStart();
        verify(handler, times(2)).onFinish();
        verifyNoMoreInteractions(handler);

        verify(locationFactory).newLocation();
        verifyNoMoreInteractions(locationFactory);
    }

    @Test
    public void testParseLocationRMCRMC() throws Exception {
        when(locationFactory.newLocation()).thenReturn(location);

        parser.parse("$GPRMC,163407.000,A,5004.7485,N,01423.8956,E,0.04,36.97,180416,,*38");
        parser.parse("$GPRMC,163407.000,A,5004.7485,N,01423.8956,E,0.04,36.97,180416,,*38");

        verify(handler, times(2)).onStart();
        verify(handler, times(2)).onFinish();
        verifyNoMoreInteractions(handler);

        verify(locationFactory).newLocation();
        verifyNoMoreInteractions(locationFactory);
    }

    @Test
    public void testParseLocationRMCGGAGGASameTime() throws Exception {
        when(locationFactory.newLocation()).thenReturn(location, mock(Location.class));

        parser.parse("$GPRMC,163407.000,A,5004.7485,N,01423.8956,E,0.04,36.97,180416,,*38");
        parser.parse("$GPGGA,163407.000,5004.7485,N,01423.8956,E,1,07,1.7,285.7,M,45.5,M,,0000*5F");
        parser.parse("$GPGGA,163407.000,5004.7485,N,01423.8956,E,1,07,1.7,285.7,M,45.5,M,,0000*5F");

        verify(handler, times(3)).onStart();
        verify(handler, times(3)).onFinish();
        verify(handler).onLocation(location);
        verifyNoMoreInteractions(handler);

        verify(location).setTime(eq(1460954639384L));
        verify(location).setLatitude(doubleThat(roughlyEq(52.14583)));
        verify(location).setLongitude(doubleThat(roughlyEq(16.87111)));
        verify(location).setAltitude(doubleThat(roughlyEq(240.2)));
        verify(location).setAccuracy(floatThat(roughlyEq(6.8f)));
        verify(location).setSpeed(floatThat(roughlyEq(0.02057f)));
        verify(location).setBearing(floatThat(roughlyEq(36.97f)));
        verifyNoMoreInteractions(location);

        verify(locationFactory, times(2)).newLocation();
        verifyNoMoreInteractions(locationFactory);
    }

    @Test
    public void testParseLocationRMCGGARMCSameTime() throws Exception {
        when(locationFactory.newLocation()).thenReturn(location, mock(Location.class));

        parser.parse("$GPRMC,163407.000,A,5004.7485,N,01423.8956,E,0.04,36.97,180416,,*38");
        parser.parse("$GPGGA,163407.000,5004.7485,N,01423.8956,E,1,07,1.7,285.7,M,45.5,M,,0000*5F");
        parser.parse("$GPRMC,163407.000,A,5004.7485,N,01423.8956,E,0.04,36.97,180416,,*38");

        verify(handler, times(3)).onStart();
        verify(handler, times(3)).onFinish();
        verify(handler).onLocation(location);
        verifyNoMoreInteractions(handler);

        verify(location).setTime(eq(1460954639384L));
        verify(location).setLatitude(doubleThat(roughlyEq(52.14583)));
        verify(location).setLongitude(doubleThat(roughlyEq(16.87111)));
        verify(location).setAltitude(doubleThat(roughlyEq(240.2)));
        verify(location).setAccuracy(floatThat(roughlyEq(6.8f)));
        verify(location).setSpeed(floatThat(roughlyEq(0.02057f)));
        verify(location).setBearing(floatThat(roughlyEq(36.97f)));
        verifyNoMoreInteractions(location);

        verify(locationFactory, times(2)).newLocation();
        verifyNoMoreInteractions(locationFactory);
    }
}