package com.github.petr_s.nmea;


public class GpsSatellite {
    boolean mHasEphemeris;
    boolean mHasAlmanac;
    boolean mUsedInFix;
    int mPrn;
    float mSnr;
    float mElevation;
    float mAzimuth;

    public GpsSatellite(int prn) {
        mPrn = prn;
    }

    public void setHasEphemeris(boolean hasEphemeris) {
        this.mHasEphemeris = hasEphemeris;
    }

    public void setHasAlmanac(boolean hasAlmanac) {
        this.mHasAlmanac = hasAlmanac;
    }

    public void setUsedInFix(boolean usedInFix) {
        this.mUsedInFix = usedInFix;
    }

    /**
     * Returns the PRN (pseudo-random number) for the satellite.
     *
     * @return PRN number
     */
    public int getPrn() {
        return mPrn;
    }

    /**
     * Returns the signal to noise ratio for the satellite.
     *
     * @return the signal to noise ratio
     */
    public float getSnr() {
        return mSnr;
    }

    public void setSnr(float snr) {
        this.mSnr = snr;
    }

    /**
     * Returns the elevation of the satellite in degrees.
     * The elevation can vary between 0 and 90.
     *
     * @return the elevation in degrees
     */
    public float getElevation() {
        return mElevation;
    }

    public void setElevation(float elevation) {
        this.mElevation = elevation;
    }

    /**
     * Returns the azimuth of the satellite in degrees.
     * The azimuth can vary between 0 and 360.
     *
     * @return the azimuth in degrees
     */
    public float getAzimuth() {
        return mAzimuth;
    }

    public void setAzimuth(float azimuth) {
        this.mAzimuth = azimuth;
    }

    /**
     * Returns true if the GPS engine has ephemeris data for the satellite.
     *
     * @return true if the satellite has ephemeris data
     */
    public boolean hasEphemeris() {
        return mHasEphemeris;
    }

    /**
     * Returns true if the GPS engine has almanac data for the satellite.
     *
     * @return true if the satellite has almanac data
     */
    public boolean hasAlmanac() {
        return mHasAlmanac;
    }

    /**
     * Returns true if the satellite was used by the GPS engine when
     * calculating the most recent GPS fix.
     *
     * @return true if the satellite was used to compute the most recent fix.
     */
    public boolean usedInFix() {
        return mUsedInFix;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        GpsSatellite satellite = (GpsSatellite) o;

        if (mHasEphemeris != satellite.mHasEphemeris) return false;
        if (mHasAlmanac != satellite.mHasAlmanac) return false;
        if (mUsedInFix != satellite.mUsedInFix) return false;
        if (mPrn != satellite.mPrn) return false;
        if (Float.compare(satellite.mSnr, mSnr) != 0) return false;
        if (Float.compare(satellite.mElevation, mElevation) != 0) return false;
        return Float.compare(satellite.mAzimuth, mAzimuth) == 0;

    }

    @Override
    public int hashCode() {
        int result = (mHasEphemeris ? 1 : 0);
        result = 31 * result + (mHasAlmanac ? 1 : 0);
        result = 31 * result + (mUsedInFix ? 1 : 0);
        result = 31 * result + mPrn;
        result = 31 * result + (mSnr != +0.0f ? Float.floatToIntBits(mSnr) : 0);
        result = 31 * result + (mElevation != +0.0f ? Float.floatToIntBits(mElevation) : 0);
        result = 31 * result + (mAzimuth != +0.0f ? Float.floatToIntBits(mAzimuth) : 0);
        return result;
    }

    @Override
    public String toString() {
        return "GpsSatellite{" +
                "mHasEphemeris=" + mHasEphemeris +
                ", mHasAlmanac=" + mHasAlmanac +
                ", mUsedInFix=" + mUsedInFix +
                ", mPrn=" + mPrn +
                ", mSnr=" + mSnr +
                ", mElevation=" + mElevation +
                ", mAzimuth=" + mAzimuth +
                '}';
    }
}
