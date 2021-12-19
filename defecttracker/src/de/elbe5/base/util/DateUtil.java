package de.elbe5.base.util;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

public class DateUtil {

    public static Date asDate(LocalDate localDate) {
        if (localDate==null)
            return null;
        return Date.from(localDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
    }

    public static Date asDate(LocalDateTime localDateTime) {
        if (localDateTime==null)
            return null;
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    public static LocalDate asLocalDate(Date date) {
        if (date==null)
            return null;
        return Instant.ofEpochMilli(date.getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
    }

    public static LocalDateTime asLocalDateTime(Date date) {
        if (date==null)
            return null;
        return Instant.ofEpochMilli(date.getTime()).atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

    public static long asMillis(LocalDate localDate) {
        if (localDate==null)
            return 0;
        return asDate(localDate).getTime();
    }

    public static long asMillis(LocalDateTime localDateTime) {
        if (localDateTime==null)
            return 0;
        return asDate(localDateTime).getTime();
    }

    public static LocalDate asLocalDate(long millis) {
        if (millis==0)
            return null;
        return Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()).toLocalDate();
    }

    public static LocalDateTime asLocalDateTime(long millis) {
        if (millis==0)
            return null;
        return Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

}
