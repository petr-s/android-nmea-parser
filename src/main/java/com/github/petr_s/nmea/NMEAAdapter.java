package com.github.petr_s.nmea;

import android.location.Location;

import java.util.List;

public class NMEAAdapter implements NMEAHandler {
    @Override
    public void onStart() {

    }

    @Override
    public void onLocation(Location location) {

    }

    @Override
    public void onSatellites(List<GpsSatellite> satellites) {

    }

    @Override
    public void onUnrecognized(String sentence) {

    }

    @Override
    public void onBadChecksum(int expected, int actual) {

    }

    @Override
    public void onException(Exception e) {

    }

    @Override
    public void onFinish() {

    }
}
