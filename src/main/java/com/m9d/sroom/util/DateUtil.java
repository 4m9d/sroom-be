package com.m9d.sroom.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Date Utility
 */
@Slf4j
@Service
public class DateUtil {

    //time format
    public static final int MINUTES_IN_HOUR = 60;
    public static final int SECONDS_IN_MINUTE = 60;
    public static final int DAYS_IN_WEEK = 7;
    public static final String FORMAT_WITH_HOUR = "%d:%02d:%02d";
    public static final String FORMAT_WITHOUT_HOUR = "%d:%02d";

    public static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public Long convertTimeToSeconds(String time) {
        String[] parts = time.split(":");
        int length = parts.length;

        if (length == 2) {
            return Long.parseLong(parts[0]) * 60 + Long.parseLong(parts[1]);
        } else if (length == 3) {
            return Long.parseLong(parts[0]) * 3600 + Long.parseLong(parts[1]) * 60 + Long.parseLong(parts[2]);
        } else {
            throw new IllegalArgumentException("duration time 포멧이 적절하지 않습니다.");
        }
    }

    public String formatDuration(String durationString) {
        Duration duration = Duration.parse(durationString);

        long totalSeconds = duration.getSeconds();
        long hours = totalSeconds / (MINUTES_IN_HOUR * SECONDS_IN_MINUTE);
        long minutes = (totalSeconds % (MINUTES_IN_HOUR * SECONDS_IN_MINUTE)) / SECONDS_IN_MINUTE;
        long seconds = totalSeconds % SECONDS_IN_MINUTE;

        if (hours > 0) {
            return String.format(FORMAT_WITH_HOUR, hours, minutes, seconds);
        } else {
            return String.format(FORMAT_WITHOUT_HOUR, minutes, seconds);
        }
    }

    public int convertISOToSeconds(String isoTime) {
        Duration duration = Duration.parse(isoTime);

        int totalSeconds = (int) duration.toSeconds();
        return totalSeconds;
    }

    public Date convertStringToDate(String strDate) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        try {
            return format.parse(strDate);
        } catch (ParseException e) {
            throw new IllegalArgumentException("입력한 날짜 형식이 올바르지 않습니다.");
        }
    }

    public boolean validateExpiration(Timestamp time, long updateThresholdHours) {
        LocalDateTime updatedAt = LocalDateTime.ofInstant(time.toInstant(), ZoneId.systemDefault());
        if (updatedAt.isAfter(LocalDateTime.now().minusHours(updateThresholdHours))) {
            return true;
        }

        return false;
    }

    public Timestamp convertISOToTimestamp(String isoString) {
        try {
            Instant instant = Instant.parse(isoString);
            return Timestamp.from(instant);
        } catch (DateTimeParseException e) {
            e.printStackTrace();
            return null;
        }
    }


    /**
     * TimeZone
     */
    private TimeZone timeZone;

    private Calendar calendar;
    private SimpleDateFormat sdf;

    public DateUtil() {
        timeZone = TimeZone.getTimeZone("Asia/Seoul");
        calendar = Calendar.getInstance();
        sdf = new SimpleDateFormat("yyyy-MM-dd");
    }

    /**
     * 2개 시간의 차이를 초 단위로 구한다.
     *
     * @param startTime 시작 시간
     * @param endTime   종료 시간
     * @return long        시작 시간과 종료 시간 사이의 초를 구한다.
     */
    public int getDiffSeconds(Date startTime, Date endTime) {
        int dueSeconds = (int) Math.ceil((endTime.getTime() - startTime.getTime()) / 1000);
        log.info("getDiffSeconds={}", dueSeconds);
        return dueSeconds;
    }

    /**
     * 2개 시간의 차이를 일 단위로 구한다.
     *
     * @param startTime 시작 시간
     * @param endTime   종료 시간
     * @return long        시작 시간과 종료 시간 사이의 일을 구한다.
     */
    public int getDiffDays(Date startTime, Date endTime) {
        log.info("endTime={}, startTime={}", endTime.getTime(), startTime.getTime());
        int diffDays = (int) (Math.ceil((endTime.getTime() - startTime.getTime()) / (1000 * 60 * 60 * 24)) + 1);
        log.info("getDiffDays={}", diffDays);
        return diffDays;
    }

    /**
     * 시작 시간으로부터 현재 시간까지의 소요시간을 구해, 로그를 출력한다.
     *
     * @param startTime 시작 시간
     */
    public void measureTime(String jobName, Date startTime) {
        Date endTime = new Date();
        int dueSeconds = getDiffSeconds(startTime, endTime);
        log.info("{}: startTime={}, endTime={}, dueSeconds={}", jobName, startTime, endTime, dueSeconds);
    }

}
