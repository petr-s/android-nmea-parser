package com.github.petr_s.nmea;

public interface NMEAHandler {
    void onStart();

    /***
     * Called on GPRMC parsed.
     *
     * @param dateTime  milliseconds since midnight, January 1, 1970 UTC.
     * @param latitude  angular y position on the Earth.
     * @param longitude angular x position on the Earth.
     * @param speed     in meters per second.
     * @param direction angular bearing value to the North.
     */
    void onRMC(long dateTime, double latitude, double longitude, float speed, float direction);

    /***
     * Called on GPGGA parsed.
     *
     * @param time        actual UTC time (without date)
     * @param latitude    angular y position on the Earth.
     * @param longitude   angular x position on the Earth.
     * @param quality     TODO
     * @param satellites  actual number of satellites
     * @param hdop        horizontal dilution of precision
     * @param altitude    altitude in meters above geoid
     * @param geoidHeight TODO
     */
    void onGGA(long time, double latitude, double longitude, int quality, int satellites, float hdop, float altitude, float geoidHeight);

    void onUnrecognized(String sentence);

    void onBadChecksum(int expected, int actual);

    void onException(Exception e);

    void onFinished();
}
