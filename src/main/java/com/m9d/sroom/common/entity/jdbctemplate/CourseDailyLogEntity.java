package com.m9d.sroom.common.entity.jdbctemplate;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.jdbc.core.RowMapper;

import java.util.Date;

@Getter
@Setter
@Builder
public class CourseDailyLogEntity {

    private Long courseDailyLogId;

    private Long memberId;

    private Long courseId;

    private Date dailyLogDate;

    private int learningTime;

    private int quizCount;

    private int lectureCount;

    public static RowMapper<CourseDailyLogEntity> getRowMapper() {
        return ((rs, rowNum) -> CourseDailyLogEntity.builder()
                .courseDailyLogId(rs.getLong("course_daily_log_id"))
                .memberId(rs.getLong("member_id"))
                .courseId(rs.getLong("course_id"))
                .dailyLogDate(rs.getDate("daily_log_date"))
                .learningTime(rs.getInt("learning_time"))
                .quizCount(rs.getInt("quiz_count"))
                .lectureCount(rs.getInt("lecture_count"))
                .build());
    }
}
