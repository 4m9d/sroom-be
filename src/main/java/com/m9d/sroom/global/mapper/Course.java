package com.m9d.sroom.global.mapper;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.jdbc.core.RowMapper;

import java.sql.Timestamp;
import java.util.Date;

@Data
@Builder
public class Course {

    private Long courseId;

    private Long memberId;

    private int videoCount;

    private String courseTitle;

    private String lectureCount;

    private String thumbnail;

    private int duration;

    private Date expectedEndDate;

    private Integer dailyTargetTime;

    private Integer weeks;

    private boolean scheduled;

    private Date startDate;

    private Timestamp lastViewTime;

    private Integer progress;

    public static RowMapper<Course> getRowMapper() {
        return (rs, rowNum) -> Course.builder()
                .courseId(rs.getLong("course_id"))
                .memberId(rs.getLong("member_id"))
                .courseTitle(rs.getString("course_title"))
                .duration(rs.getInt("course_duration"))
                .lastViewTime(rs.getTimestamp("last_view_time"))
                .progress(rs.getInt("progress"))
                .thumbnail(rs.getString("thumbnail"))
                .scheduled(rs.getBoolean("is_scheduled"))
                .weeks(rs.getInt("weeks"))
                .expectedEndDate(rs.getDate("expected_end_date"))
                .dailyTargetTime(rs.getInt("daily_target_time"))
                .startDate(rs.getDate("start_date"))
                .build();
    }
}
