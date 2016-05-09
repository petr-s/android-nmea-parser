package com.github.petr_s.nmea;

public interface NMEAHandler {
    void onStart();

    /***
     * Called on GPRMC parsed.
     *
     * @param date      milliseconds since midnight, January 1, 1970 UTC.
     * @param time      actual UTC time (without date)
     * @param latitude  angular y position on the Earth.
     * @param longitude angular x position on the Earth.
     * @param speed     in meters per second.
     * @param direction angular bearing value to the North.
     */
    void onRMC(long date, long time, double latitude, double longitude, float speed, float direction);

    /***
     * Called on GPGGA parsed.
     *
     * @param time        actual UTC time (without date)
     * @param latitude    angular y position on the Earth.
     * @param longitude   angular x position on the Earth.
     * @param altitude    altitude in meters above corrected geoid
     * @param quality     fix-quality type {@link FixQuality}
     * @param satellites  actual number of satellites
     * @param hdop        horizontal dilution of precision
     */
    void onGGA(long time, double latitude, double longitude, float altitude, FixQuality quality, int satellites, float hdop);

    void onUnrecognized(String sentence);

    void onBadChecksum(int expected, int actual);

    void onException(Exception e);

    void onFinished();

    enum FixQuality {
        Invalid(0),
        GPS(1),
        DGPS(2),
        PPS(3),
        IRTK(4),
        FRTK(5),
        Estimated(6),
        Manual(7),
        Simulation(8);

        public final int value;

        FixQuality(int value) {
            this.value = value;
        }
    }
}
