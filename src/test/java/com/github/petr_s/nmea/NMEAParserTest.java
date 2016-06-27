package com.github.petr_s.nmea;

import android.location.Location;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Arrays;

import static com.github.petr_s.nmea.Helper.*;
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
        verify(location).setLatitude(doubleThat(roughlyEq(50.07914)));
        verify(location).setLongitude(doubleThat(roughlyEq(14.39825)));
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
        verify(location).setLatitude(doubleThat(roughlyEq(50.079141)));
        verify(location).setLongitude(doubleThat(roughlyEq(14.39825)));
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
        verify(location).setLatitude(doubleThat(roughlyEq(50.079141)));
        verify(location).setLongitude(doubleThat(roughlyEq(14.39825)));
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
        verify(location).setLatitude(doubleThat(roughlyEq(50.07914)));
        verify(location).setLongitude(doubleThat(roughlyEq(14.39825)));
        verify(location).setAltitude(doubleThat(roughlyEq(240.2)));
        verify(location).setAccuracy(floatThat(roughlyEq(6.8f)));
        verify(location).setSpeed(floatThat(roughlyEq(0.02057f)));
        verify(location).setBearing(floatThat(roughlyEq(36.97f)));
        verifyNoMoreInteractions(location);

        verify(locationFactory, times(2)).newLocation();
        verifyNoMoreInteractions(locationFactory);
    }

    private GpsSatellite newSatellite(int prn, float elevation, float azimuth, int snr, boolean fix) {
        GpsSatellite satellite = new GpsSatellite(prn);
        satellite.setAzimuth(azimuth);
        satellite.setElevation(elevation);
        satellite.setSnr(snr);
        satellite.setUsedInFix(fix);
        satellite.setHasAlmanac(true);
        satellite.setHasEphemeris(true);

        return satellite;
    }

    @Test
    public void testParseSatelliteGSVGSA() throws Exception {
        parser.parse("$GPGSV,3,1,11,29,86,273,30,25,60,110,38,31,52,278,47,02,28,050,39*7D");
        parser.parse("$GPGSA,A,3,25,02,26,05,29,31,21,12,,,,,1.6,1.0,1.3*3B");

        verify(handler, times(2)).onStart();
        verify(handler, times(2)).onFinish();
        verifyNoMoreInteractions(handler);
    }

    @Test
    public void testParseSatellite3GSVGGA() throws Exception {
        parser.parse("$GPGSV,3,1,11,29,86,273,30,25,60,110,38,31,52,278,47,02,28,050,39*7D");
        parser.parse("$GPGSV,3,2,11,12,23,110,34,26,18,295,29,21,17,190,30,05,11,092,25*72");
        parser.parse("$GPGSV,3,3,11,14,02,232,13,23,02,346,12,20,01,135,13*48");
        parser.parse("$GPGSA,A,3,25,02,26,05,29,31,21,12,,,,,1.6,1.0,1.3*3B");

        verify(handler, times(4)).onStart();
        verify(handler, times(4)).onFinish();
        verify(handler).onSatellites(argThat(eq(Arrays.asList(new GpsSatellite[]{
                newSatellite(29, 86.0f, 273.0f, 30, true),
                newSatellite(25, 60.0f, 110.0f, 38, true),
                newSatellite(31, 52.0f, 278.0f, 47, true),
                newSatellite(2, 28.0f, 50.0f, 39, true),
                newSatellite(12, 23.0f, 110.0f, 34, true),
                newSatellite(26, 18.0f, 295.0f, 29, true),
                newSatellite(21, 17.0f, 190.0f, 30, true),
                newSatellite(5, 11.0f, 92.0f, 25, true),
                newSatellite(14, 2.0f, 232.0f, 13, false),
                newSatellite(23, 2.0f, 346.0f, 12, false),
                newSatellite(20, 1.0f, 135.0f, 13, false)}))));
        verifyNoMoreInteractions(handler);
    }

    @Test
    public void testParseSatelliteGGA3GSV() throws Exception {
        parser.parse("$GPGSA,A,3,25,02,26,05,29,31,21,12,,,,,1.6,1.0,1.3*3B");
        parser.parse("$GPGSV,3,1,11,29,86,273,30,25,60,110,38,31,52,278,47,02,28,050,39*7D");
        parser.parse("$GPGSV,3,2,11,12,23,110,34,26,18,295,29,21,17,190,30,05,11,092,25*72");
        parser.parse("$GPGSV,3,3,11,14,02,232,13,23,02,346,12,20,01,135,13*48");

        verify(handler, times(4)).onStart();
        verify(handler, times(4)).onFinish();
        verify(handler).onSatellites(argThat(eq(Arrays.asList(new GpsSatellite[]{
                newSatellite(29, 86.0f, 273.0f, 30, true),
                newSatellite(25, 60.0f, 110.0f, 38, true),
                newSatellite(31, 52.0f, 278.0f, 47, true),
                newSatellite(2, 28.0f, 50.0f, 39, true),
                newSatellite(12, 23.0f, 110.0f, 34, true),
                newSatellite(26, 18.0f, 295.0f, 29, true),
                newSatellite(21, 17.0f, 190.0f, 30, true),
                newSatellite(5, 11.0f, 92.0f, 25, true),
                newSatellite(14, 2.0f, 232.0f, 13, false),
                newSatellite(23, 2.0f, 346.0f, 12, false),
                newSatellite(20, 1.0f, 135.0f, 13, false)}))));
        verifyNoMoreInteractions(handler);
    }

    @Test
    public void testParseSatellite2GSVGGAGSV() throws Exception {
        parser.parse("$GPGSV,3,1,11,29,86,273,30,25,60,110,38,31,52,278,47,02,28,050,39*7D");
        parser.parse("$GPGSV,3,2,11,12,23,110,34,26,18,295,29,21,17,190,30,05,11,092,25*72");
        parser.parse("$GPGSA,A,3,25,02,26,05,29,31,21,12,,,,,1.6,1.0,1.3*3B");
        parser.parse("$GPGSV,3,3,11,14,02,232,13,23,02,346,12,20,01,135,13*48");

        verify(handler, times(4)).onStart();
        verify(handler, times(4)).onFinish();
        verify(handler).onSatellites(argThat(eq(Arrays.asList(new GpsSatellite[]{
                newSatellite(29, 86.0f, 273.0f, 30, true),
                newSatellite(25, 60.0f, 110.0f, 38, true),
                newSatellite(31, 52.0f, 278.0f, 47, true),
                newSatellite(2, 28.0f, 50.0f, 39, true),
                newSatellite(12, 23.0f, 110.0f, 34, true),
                newSatellite(26, 18.0f, 295.0f, 29, true),
                newSatellite(21, 17.0f, 190.0f, 30, true),
                newSatellite(5, 11.0f, 92.0f, 25, true),
                newSatellite(14, 2.0f, 232.0f, 13, false),
                newSatellite(23, 2.0f, 346.0f, 12, false),
                newSatellite(20, 1.0f, 135.0f, 13, false)}))));
        verifyNoMoreInteractions(handler);
    }
}