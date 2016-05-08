package com.github.petr_s.nmea;

public class NMEAAdapter implements NMEAHandler {
    @Override
    public void onStart() {

    }

    @Override
    public void onRMC(long dateTime, double latitude, double longitude, float speed, float direction) {

    }

    @Override
    public void onGGA(long time, double latitude, double longitude, int quality, int satellites, float hdop, float altitude, float geoidHeight) {

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
    public void onFinished() {

    }
}
