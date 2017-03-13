package com.github.petr_s.nmea.basic;

import com.github.petr_s.nmea.basic.BasicNMEAHandler.FixQuality;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.github.petr_s.nmea.basic.BasicNMEAHandler.FixType;

public class BasicNMEAParser {
    private static final float KNOTS2MPS = 0.514444f;
    private static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("HHmmss", Locale.US);
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("ddMMyy", Locale.US);
    private static final String COMMA = ",";
    private static final String CAP_FLOAT = "(\\d*[.]?\\d+)";
    private static final String CAP_FLOAT_ALT = "(-?\\d*[.]?\\d+)";
    private static final String HEX_INT = "[0-9a-fA-F]";
    private static final Pattern GENERAL_SENTENCE = Pattern.compile("^\\$(\\w{5}),(.*)[*](" + HEX_INT + "{2})$");
    private static final Pattern GPRMC = Pattern.compile("(\\d{5})?" +
            "(\\d[.]?\\d*)?" + COMMA +
            regexify(Status.class) + COMMA +
            "(\\d{2})(\\d{2}[.]\\d+)?" + COMMA +
            regexify(VDir.class) + "?" + COMMA +
            "(\\d{3})(\\d{2}[.]\\d+)?" + COMMA +
            regexify(HDir.class) + "?" + COMMA +
            CAP_FLOAT + "?" + COMMA +
            CAP_FLOAT + "?" + COMMA +
            "(\\d{6})?" + COMMA +
            CAP_FLOAT + "?" + COMMA +
            regexify(HDir.class) + "?" + COMMA + "?" +
            regexify(FFA.class) + "?");
    private static final Pattern GPGGA = Pattern.compile("(\\d{5})?" +
            "(\\d[.]?\\d*)?" + COMMA +
            "(\\d{2})(\\d{2}[.]\\d+)?" + COMMA +
            regexify(VDir.class) + "?" + COMMA +
            "(\\d{3})(\\d{2}[.]\\d+)?" + COMMA +
            regexify(HDir.class) + "?" + COMMA +
            "(\\d)?" + COMMA +
            "(\\d{2})?" + COMMA +
            CAP_FLOAT + "?" + COMMA +
            CAP_FLOAT_ALT + "?,[M]" + COMMA +
            CAP_FLOAT_ALT + "?,[M]" + COMMA +
            CAP_FLOAT + "?" + COMMA +
            "(\\d{4})?");
    private static final Pattern GPGSV = Pattern.compile("(\\d+)" + COMMA +
            "(\\d+)" + COMMA +
            "(\\d{2})" + COMMA +

            "(\\d{2})" + COMMA +
            "(\\d{2})" + COMMA +
            "(\\d{3})" + COMMA +
            "(\\d{2})" + COMMA +

            "(\\d{2})?" + COMMA + "?" +
            "(\\d{2})?" + COMMA + "?" +
            "(\\d{3})?" + COMMA + "?" +
            "(\\d{2})?" + COMMA + "?" +

            "(\\d{2})?" + COMMA + "?" +
            "(\\d{2})?" + COMMA + "?" +
            "(\\d{3})?" + COMMA + "?" +
            "(\\d{2})?" + COMMA + "?" +

            "(\\d{2})?" + COMMA + "?" +
            "(\\d{2})?" + COMMA + "?" +
            "(\\d{3})?" + COMMA + "?" +
            "(\\d{2})?");
    private static final Pattern GPGSA = Pattern.compile(regexify(Mode.class) + COMMA +
            "(\\d)" + COMMA +

            "(\\d{2})?" + COMMA +
            "(\\d{2})?" + COMMA +
            "(\\d{2})?" + COMMA +
            "(\\d{2})?" + COMMA +
            "(\\d{2})?" + COMMA +
            "(\\d{2})?" + COMMA +
            "(\\d{2})?" + COMMA +
            "(\\d{2})?" + COMMA +
            "(\\d{2})?" + COMMA +
            "(\\d{2})?" + COMMA +
            "(\\d{2})?" + COMMA +
            "(\\d{2})?" + COMMA +

            CAP_FLOAT + "?" + COMMA +
            CAP_FLOAT + "?" + COMMA +
            CAP_FLOAT + "?");
    private static HashMap<String, ParsingFunction> functions = new HashMap<>();

    static {
        TIME_FORMAT.setTimeZone(TimeZone.getTimeZone("UTC"));
        DATE_FORMAT.setTimeZone(TimeZone.getTimeZone("UTC"));
        functions.put("GPRMC", new ParsingFunction() {
            @Override
            public boolean parse(BasicNMEAHandler handler, String sentence) throws Exception {
                return parseGPRMC(handler, sentence);
            }
        });
        functions.put("GPGGA", new ParsingFunction() {
            @Override
            public boolean parse(BasicNMEAHandler handler, String sentence) throws Exception {
                return parseGPGGA(handler, sentence);
            }
        });
        functions.put("GPGSV", new ParsingFunction() {
            @Override
            public boolean parse(BasicNMEAHandler handler, String sentence) throws Exception {
                return parseGPGSV(handler, sentence);
            }
        });
        functions.put("GPGSA", new ParsingFunction() {
            @Override
            public boolean parse(BasicNMEAHandler handler, String sentence) throws Exception {
                return parseGPGSA(handler, sentence);
            }
        });
    }

    private final BasicNMEAHandler handler;

    public BasicNMEAParser(BasicNMEAHandler handler) {
        this.handler = handler;

        if (handler == null) {
            throw new NullPointerException();
        }
    }

    private static boolean parseGPRMC(BasicNMEAHandler handler, String sentence) throws Exception {
        ExMatcher matcher = new ExMatcher(GPRMC.matcher(sentence));
        if (matcher.matches()) {
            long time = TIME_FORMAT.parse(matcher.nextString("time") + "0").getTime();
            Float ms = matcher.nextFloat("time-ms");
            if (ms != null) {
                time += ms * 1000;
            }
            if (Status.valueOf(matcher.nextString("status")) == Status.A) {
                double latitude = toDegrees(matcher.nextInt("degrees"),
                        matcher.nextFloat("minutes"));
                VDir vDir = VDir.valueOf(matcher.nextString("vertical-direction"));
                double longitude = toDegrees(matcher.nextInt("degrees"),
                        matcher.nextFloat("minutes"));
                HDir hDir = HDir.valueOf(matcher.nextString("horizontal-direction"));
                float speed = matcher.nextFloat("speed") * KNOTS2MPS;
                float direction = matcher.nextFloat("direction", 0.0f);
                long date = DATE_FORMAT.parse(matcher.nextString("date")).getTime();
                Float magVar = matcher.nextFloat("magnetic-variation");
                String magVarDir = matcher.nextString("direction");
                String faa = matcher.nextString("faa");

                handler.onRMC(date,
                        time,
                        vDir.equals(VDir.N) ? latitude : -latitude,
                        hDir.equals(HDir.E) ? longitude : -longitude,
                        speed,
                        direction);

                return true;
            }
        }

        return false;
    }

    private static boolean parseGPGGA(BasicNMEAHandler handler, String sentence) throws Exception {
        ExMatcher matcher = new ExMatcher(GPGGA.matcher(sentence));
        if (matcher.matches()) {
            long time = TIME_FORMAT.parse(matcher.nextString("time") + "0").getTime();
            Float ms = matcher.nextFloat("time-ms");
            if (ms != null) {
                time += ms * 1000;
            }
            double latitude = toDegrees(matcher.nextInt("degrees"),
                    matcher.nextFloat("minutes"));
            VDir vDir = VDir.valueOf(matcher.nextString("vertical-direction"));
            double longitude = toDegrees(matcher.nextInt("degrees"),
                    matcher.nextFloat("minutes"));
            HDir hDir = HDir.valueOf(matcher.nextString("horizontal-direction"));
            FixQuality quality = FixQuality.values()[matcher.nextInt("quality")];
            int satellites = matcher.nextInt("n-satellites");
            float hdop = matcher.nextFloat("hdop");
            float altitude = matcher.nextFloat("altitude");
            float separation = matcher.nextFloat("separation");
            Float age = matcher.nextFloat("age");
            Integer station = matcher.nextInt("station");

            handler.onGGA(time,
                    vDir.equals(VDir.N) ? latitude : -latitude,
                    hDir.equals(HDir.E) ? longitude : -longitude,
                    altitude - separation,
                    quality,
                    satellites,
                    hdop);

            return true;
        }

        return false;
    }

    private static boolean parseGPGSV(BasicNMEAHandler handler, String sentence) throws Exception {
        ExMatcher matcher = new ExMatcher(GPGSV.matcher(sentence));
        if (matcher.matches()) {
            int sentences = matcher.nextInt("n-sentences");
            int index = matcher.nextInt("sentence-index") - 1;
            int satellites = matcher.nextInt("n-satellites");

            for (int i = 0; i < 4; i++) {
                Integer prn = matcher.nextInt("prn");
                Integer elevation = matcher.nextInt("elevation");
                Integer azimuth = matcher.nextInt("azimuth");
                Integer snr = matcher.nextInt("snr");

                if (prn != null) {
                    handler.onGSV(satellites, index * 4 + i, prn, elevation, azimuth, snr);
                }
            }

            return true;
        }
        return false;
    }

    private static boolean parseGPGSA(BasicNMEAHandler handler, String sentence) {
        ExMatcher matcher = new ExMatcher(GPGSA.matcher(sentence));
        if (matcher.matches()) {
            Mode mode = Mode.valueOf(matcher.nextString("mode"));
            FixType type = FixType.values()[matcher.nextInt("fix-type")];
            Set<Integer> prns = new HashSet<>();
            for (int i = 0; i < 12; i++) {
                Integer prn = matcher.nextInt("prn");
                if (prn != null) {
                    prns.add(prn);
                }
            }
            float pdop = matcher.nextFloat("pdop");
            float hdop = matcher.nextFloat("hdop");
            float vdop = matcher.nextFloat("vdop");

            handler.onGSA(type, prns, pdop, hdop, vdop);

            return true;
        }
        return false;
    }

    private static int calculateChecksum(String sentence) throws UnsupportedEncodingException {
        byte[] bytes = sentence.substring(1, sentence.length() - 3).getBytes("US-ASCII");
        int checksum = 0;
        for (byte b : bytes) {
            checksum ^= b;
        }
        return checksum;
    }

    private static double toDegrees(int degrees, float minutes) {
        return degrees + minutes / 60.0;
    }

    private static <T extends Enum<T>> String regexify(Class<T> clazz) {
        StringBuilder sb = new StringBuilder();
        sb.append("([");
        for (T c : clazz.getEnumConstants()) {
            sb.append(c.toString());
        }
        sb.append("])");

        return sb.toString();
    }

    public synchronized void parse(String sentence) {
        if (sentence == null) {
            throw new NullPointerException();
        }

        handler.onStart();
        try {
            ExMatcher matcher = new ExMatcher(GENERAL_SENTENCE.matcher(sentence));
            if (matcher.matches()) {
                String type = matcher.nextString("type");
                String content = matcher.nextString("content");
                int expected_checksum = matcher.nextHexInt("checksum");
                int actual_checksum = calculateChecksum(sentence);

                if (actual_checksum != expected_checksum) {
                    handler.onBadChecksum(expected_checksum, actual_checksum);
                } else if (!functions.containsKey(type) || !functions.get(type).parse(handler, content)) {
                    handler.onUnrecognized(sentence);
                }
            } else {
                handler.onUnrecognized(sentence);
            }
        } catch (Exception e) {
            handler.onException(e);
        } finally {
            handler.onFinished();
        }
    }

    private enum Status {
        A,
        V
    }

    private enum HDir {
        E,
        W
    }

    private enum VDir {
        N,
        S,
    }

    private enum Mode {
        A,
        M
    }

    private enum FFA {
        A,
        D,
        E,
        M,
        S,
        N
    }

    private static abstract class ParsingFunction {
        public abstract boolean parse(BasicNMEAHandler handler, String sentence) throws Exception;
    }

    private static class ExMatcher {
        Matcher original;
        int index;

        ExMatcher(Matcher original) {
            this.original = original;
            reset();
        }

        void reset() {
            index = 1;
        }

        boolean matches() {
            return original.matches();
        }

        String nextString(String name) {
            return original.group(index++);
        }

        Float nextFloat(String name, Float defaultValue) {
            Float next = nextFloat(name);
            return next == null ? defaultValue : next;
        }

        Float nextFloat(String name) {
            String next = nextString(name);
            return next == null ? null : Float.parseFloat(next);
        }

        Integer nextInt(String name) {
            String next = nextString(name);
            return next == null ? null : Integer.parseInt(next);
        }

        Integer nextHexInt(String name) {
            String next = nextString(name);
            return next == null ? null : Integer.parseInt(next, 16);
        }
    }
}
