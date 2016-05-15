package com.github.petr_s.nmea.basic;

public class BasicNMEAAdapter implements BasicNMEAHandler {
    @Override
    public void onStart() {

    }

    @Override
    public void onRMC(long date, long time, double latitude, double longitude, float speed, float direction) {

    }

    @Override
    public void onGGA(long time, double latitude, double longitude, float altitude, FixQuality quality, int satellites, float hdop) {

    }

    @Override
    public void onGSV(int satellites, int index, int prn, float elevation, float azimuth, int snr) {

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
