package com.m9d.sroom.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Date;

@Component
@Slf4j
public class DateUtil {

    public static final int MINUTES_IN_HOUR = 60;
    public static final int SECONDS_IN_MINUTE = 60;
    public static final int DAYS_IN_WEEK = 7;

    public static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd").withZone(ZoneId.of("Asia/Seoul"));
    public static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.of("Asia/Seoul"));


    public static Long convertTimeToSeconds(String time) {
        String[] parts = time.split(":");
        int length = parts.length;

        if (length == 2) {
            return Long.parseLong(parts[0]) * SECONDS_IN_MINUTE + Long.parseLong(parts[1]);
        } else if (length == 3) {
            return Long.parseLong(parts[0]) * SECONDS_IN_MINUTE * MINUTES_IN_HOUR + Long.parseLong(parts[1]) * SECONDS_IN_MINUTE + Long.parseLong(parts[2]);
        } else {
            throw new IllegalArgumentException("duration time 포멧이 적절하지 않습니다.");
        }
    }

    public static int convertISOToSeconds(String isoTime) {
        Duration duration = Duration.parse(isoTime);

        int totalSeconds = (int) duration.toSeconds();
        return totalSeconds;
    }

    public static Date convertStringToDate(String strDate) {
        try {
            java.time.LocalDate localDate = LocalDate.parse(strDate, dateFormatter);
            return java.sql.Date.valueOf(localDate);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("The date format you entered is invalid.", e);
        }
    }

    public static boolean hasRecentUpdate(Timestamp time, long updateThresholdHours) {
        LocalDateTime updatedAt = LocalDateTime.ofInstant(time.toInstant(), ZoneId.systemDefault());
        return updatedAt.isAfter(LocalDateTime.now().minusHours(updateThresholdHours));
    }

    public boolean validateExpiration(Timestamp time, long updateThresholdHours) {
        LocalDateTime updatedAt = LocalDateTime.ofInstant(time.toInstant(), ZoneId.systemDefault());
        if (updatedAt.isAfter(LocalDateTime.now().minusHours(updateThresholdHours))) {
            return true;
        }

        return false;
    }

    public static Timestamp convertISOToTimestamp(String isoString) {
        try {
            Instant instant = Instant.parse(isoString);
            return Timestamp.from(instant);
        } catch (DateTimeParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String convertISOToString(String publishTime) {
        return ZonedDateTime.parse(publishTime).format(dateTimeFormatter);
    }
}
