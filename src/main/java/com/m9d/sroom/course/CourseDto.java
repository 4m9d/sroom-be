package com.m9d.sroom.course;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.jdbc.core.RowMapper;

import java.sql.Timestamp;
import java.util.Date;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
public class CourseDto {

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

    public CourseDto(Long memberId, Course course) {
        this.memberId = memberId;
        this.videoCount = course.getVideoCount();
        this.courseTitle = course.getTitle();
        this.thumbnail = course.getThumbnail();
        this.duration = course.getDuration();
        this.expectedEndDate = course.getExpectedEndDate();
        this.weeks = course.getWeeks();
        this.dailyTargetTime = course.getDailyTargetTime();
    }

    public static RowMapper<CourseDto> getRowMapper() {
        return (rs, rowNum) -> CourseDto.builder()
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
