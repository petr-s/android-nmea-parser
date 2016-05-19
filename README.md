# android-nmea-parser [![Build Status](https://travis-ci.org/petr-s/android-nmea-parser.svg)](https://travis-ci.org/petr-s/android-nmea-parser) [![Coverage Status](https://coveralls.io/repos/petr-s/android-nmea-parser/badge.svg?branch=master&service=github)](https://coveralls.io/github/petr-s/android-nmea-parser?branch=master)

Light-weight Android Java library for NMEA sentences parsing
## Supported sentences:
* GPRMC
* GPGGA
* GPGSV
* GPGSA

## NMEA Parser
flow parser build on top of the [BasicNMEAParser](src/main/java/com/github/petr_s/nmea/basic/BasicNMEAParser.java)
that maps raw NMEA data to useful Android objects such as [Location](https://developer.android.com/reference/android/location/Location.html) and [GpsSatellite](https://developer.android.com/reference/android/location/GpsSatellite.html)

### Location parsing
To get an Android Location object you have to parse both RMC and GGA with the same time.
```java
NMEAHandler handler = new NMEAHandler() {
    ...
    @Override
    public void onLocation(Location location) {

    }
    ...
};
NMEAParser parser = new NMEAParser(handler);
parser.parse("$GPRMC,163407.000,A,5004.7485,N,01423.8956,E,0.04,36.97,180416,,*38");
parser.parse("$GPGGA,163407.000,5004.7485,N,01423.8956,E,1,07,1.7,285.7,M,45.5,M,,0000*5F");
```

### Satellites parsing
To get a list of gps satellites you have to parse all of GSVs and at least one GSA sentence.
Since [Android GpsSatellite class](https://developer.android.com/reference/android/location/GpsSatellite.html) is inaccessible (only trough reflection),
 the package level [GpsSatellite](src/main/java/com/github/petr_s/nmea/GpsSatellite.java) is introduced.
```java
NMEAHandler handler = new NMEAHandler() {
    ...
    @Override
    public void onSatellites(List<GpsSatellite> satellites) {

    }
    ...
};
NMEAParser parser = new NMEAParser(handler);
parser.parse("$GPGSV,3,1,11,29,86,273,30,25,60,110,38,31,52,278,47,02,28,050,39*7D");
parser.parse("$GPGSV,3,2,11,12,23,110,34,26,18,295,29,21,17,190,30,05,11,092,25*72");
parser.parse("$GPGSV,3,3,11,14,02,232,13,23,02,346,12,20,01,135,13*48");
parser.parse("$GPGSA,A,3,25,02,26,05,29,31,21,12,,,,,1.6,1.0,1.3*3B");
```

if you don't need all methods there's also an [Adapter](src/main/java/com/github/petr_s/nmea/NMEAAdapter.java)

## Basic NMEA Parser
flow parser that allows you to access raw NMEA data

```java
BasicNMEAHandler handler = new BasicNMEAHandler() {
    ...
    @Override
    public void onRMC(long date, long time, double latitude, double longitude, float speed, float direction) {
    }
    ...
};
BasicNMEAParser parser = new BasicNMEAParser(handler);
parser.parse("$GPRMC,163407.000,A,5004.7485,N,01423.8956,E,0.04,36.97,180416,,*38");
```
if you don't need all methods there's also an [Adapter](src/main/java/com/github/petr_s/nmea/basic/BasicNMEAAdapter.java)

## Gradle
```
repositories {
    mavenCentral()
}

dependencies {
    compile 'com.github.petr-s:android-nmea-parser:0.3.0'
}
```

## Maven
```
<dependency>
  <groupId>com.github.petr-s</groupId>
  <artifactId>android-nmea-parser</artifactId>
  <version>0.3.0</version>
  <type>aar</type>
</dependency>
```