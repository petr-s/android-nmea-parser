package com.github.petr_s.nmea;

import android.location.Location;

import java.util.List;

public interface NMEAHandler {
    void onStart();

    void onLocation(Location location);

    void onSatellites(List<GpsSatellite> satellites);

    void onUnrecognized(String sentence);

    void onBadChecksum(int expected, int actual);

    void onException(Exception e);

    void onFinish();
}
