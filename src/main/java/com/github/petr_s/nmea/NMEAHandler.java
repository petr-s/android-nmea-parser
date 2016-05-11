package com.github.petr_s.nmea;

import android.location.Location;

public interface NMEAHandler {
    void onStart();

    void onLocation(Location location);

    void onUnrecognized(String sentence);

    void onBadChecksum(int expected, int actual);

    void onException(Exception e);

    void onFinish();
}
