package com.github.petr_s.nmea;

import android.location.Location;
import com.github.petr_s.nmea.basic.BasicNMEAHandler;
import com.github.petr_s.nmea.basic.BasicNMEAParser;

public class NMEAParser implements BasicNMEAHandler {
    public static final String LOCATION_PROVIDER_NAME = "nmea-parser";
    private final NMEAHandler handler;
    private final BasicNMEAParser coreParser;
    private final LocationFactory locationFactory;

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
        coreParser = new BasicNMEAParser(this);

        if (handler == null) {
            throw new NullPointerException();
        }
    }

    public synchronized void parse(String sentence) {
        coreParser.parse(sentence);
    }

    @Override
    public synchronized void onStart() {
        handler.onStart();
    }

    @Override
    public synchronized void onRMC(long date, long time, double latitude, double longitude, float speed, float direction) {
    }

    @Override
    public synchronized void onGGA(long time, double latitude, double longitude, float altitude, FixQuality quality, int satellites, float hdop) {
    }

    @Override
    public synchronized void onUnrecognized(String sentence) {
        // TODO..
    }

    @Override
    public synchronized void onBadChecksum(int expected, int actual) {
        // TODO..
    }

    @Override
    public synchronized void onException(Exception e) {
        // TODO..
    }

    @Override
    public synchronized void onFinished() {
        handler.onFinish();
    }
}
