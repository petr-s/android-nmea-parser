package com.github.petr_s.nmea;

import android.location.Location;
import com.github.petr_s.nmea.basic.BasicNMEAHandler;
import com.github.petr_s.nmea.basic.BasicNMEAParser;

import java.util.Arrays;
import java.util.Set;

public class NMEAParser implements BasicNMEAHandler {
    public static final String LOCATION_PROVIDER_NAME = "nmea-parser";
    private static final int FLAG_RMC = 1;
    private static final int FLAG_GGA = 2;
    private static final int LOCATION_FLAGS = FLAG_RMC | FLAG_GGA;
    private static final int SATELLITES_COUNT = 24;
    private final NMEAHandler handler;
    private final BasicNMEAParser basicParser;
    private final LocationFactory locationFactory;
    private Location location;
    private long lastTime;
    private int flags;
    private int satellitesCount;
    private GpsSatellite[] tempSatellites = new GpsSatellite[SATELLITES_COUNT];
    private Set<Integer> activeSatellites;

    public NMEAParser(NMEAHandler handler) {
        this(handler, new LocationFactory() {
            @Override
            public Location newLocation() {
                return new Location(LOCATION_PROVIDER_NAME);
            }
        });
    }

    public NMEAParser(NMEAHandler handler, LocationFactory locationFactory) {
        this.handler = handler;
        this.locationFactory = locationFactory;
        basicParser = new BasicNMEAParser(this);

        if (handler == null) {
            throw new NullPointerException();
        }
    }

    public synchronized void parse(String sentence) {
        basicParser.parse(sentence);
    }

    private void resetLocationState() {
        flags = 0;
        lastTime = 0;
    }

    private void newLocation(long time) {
        if (location == null || time != lastTime) {
            location = locationFactory.newLocation();
            resetLocationState();
        }
    }

    private void yieldLocation(long time, int flag) {
        if ((flags | flag & LOCATION_FLAGS) == LOCATION_FLAGS) {
            handler.onLocation(location);
            resetLocationState();
        } else {
            flags |= flag;
            lastTime = time;
        }
    }

    private boolean hasAllSatellites() {
        for (int i = 0; i < satellitesCount; i++) {
            if (tempSatellites[i] == null) {
                return false;
            }
        }

        return true;
    }

    private void yieldSatellites() {
        if (satellitesCount > 0 && hasAllSatellites() && activeSatellites != null) {
            for (GpsSatellite satellite : tempSatellites) {
                if (satellite == null) {
                    break;
                } else {
                    satellite.setUsedInFix(activeSatellites.contains(satellite.getPrn()));
                    satellite.setHasAlmanac(true); // TODO: ...
                    satellite.setHasEphemeris(true);  // TODO: ...
                }
            }

            handler.onSatellites(Arrays.asList(Arrays.copyOf(tempSatellites, satellitesCount)));

            Arrays.fill(tempSatellites, null);
            activeSatellites = null;
            satellitesCount = 0;
        }
    }

    private void newSatellite(int index, int count, int prn, float elevation, float azimuth, int snr) {
        if (count != satellitesCount) {
            satellitesCount = count;
        }

        GpsSatellite satellite = new GpsSatellite(prn);
        satellite.setAzimuth(azimuth);
        satellite.setElevation(elevation);
        satellite.setSnr(snr);

        tempSatellites[index] = satellite;
    }

    @Override
    public synchronized void onStart() {
        handler.onStart();
    }

    @Override
    public synchronized void onRMC(long date, long time, double latitude, double longitude, float speed, float direction) {
        newLocation(time);

        location.setTime(date | time);
        location.setSpeed(speed);
        location.setBearing(direction);

        yieldLocation(time, FLAG_RMC);
    }

    @Override
    public synchronized void onGGA(long time, double latitude, double longitude, float altitude, FixQuality quality, int satellites, float hdop) {
        newLocation(time);

        location.setLatitude(latitude);
        location.setLongitude(longitude);
        location.setAltitude(altitude);
        location.setAccuracy(hdop * 4.0f);

        yieldLocation(time, FLAG_GGA);
    }

    @Override
    public synchronized void onGSV(int satellites, int index, int prn, float elevation, float azimuth, int snr) {
        newSatellite(index, satellites, prn, elevation, azimuth, snr);

        yieldSatellites();
    }

    @Override
    public void onGSA(FixType type, Set<Integer> prns, float pdop, float hdop, float vdop) {
        activeSatellites = prns;

        yieldSatellites();
    }

    @Override
    public synchronized void onUnrecognized(String sentence) {
        handler.onUnrecognized(sentence);
    }

    @Override
    public synchronized void onBadChecksum(int expected, int actual) {
        handler.onBadChecksum(expected, actual);
    }

    @Override
    public synchronized void onException(Exception e) {
        handler.onException(e);
    }

    @Override
    public synchronized void onFinished() {
        handler.onFinish();
    }
}
