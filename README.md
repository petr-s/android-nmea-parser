# android-nmea-parser [![Build Status](https://travis-ci.org/petr-s/android-nmea-parser.svg)](https://travis-ci.org/petr-s/android-nmea-parser) [![Coverage Status](https://coveralls.io/repos/petr-s/android-nmea-parser/badge.svg?branch=master&service=github)](https://coveralls.io/github/petr-s/android-nmea-parser?branch=master)

Light-weight Android Java library for NMEA sentences parsing
## Supported sentences:
* GPRMC
* GPGGA

## NMEA Parser
note that you have to parse both RMC and GGA with the same time to get android Location object
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
    compile 'com.github.petr-s:android-nmea-parser:0.2.0'
}
```

## Maven
```
<dependency>
  <groupId>com.github.petr-s</groupId>
  <artifactId>android-nmea-parser</artifactId>
  <version>0.2.0</version>
  <type>aar</type>
</dependency>
```