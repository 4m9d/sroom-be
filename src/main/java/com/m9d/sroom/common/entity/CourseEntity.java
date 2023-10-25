package com.m9d.sroom.common.entity;

import com.m9d.sroom.course.vo.Course;
import com.m9d.sroom.course.vo.CourseVideo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.jdbc.core.RowMapper;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class CourseEntity {

    private Long courseId;

    private Long memberId;

    private String courseTitle;

    private String thumbnail;

    private int duration;

    private Date expectedEndDate;

    private Integer dailyTargetTime;

    private Integer weeks;

    private boolean scheduled;

    private Date startDate;

    private Timestamp lastViewTime;

    private Integer progress;

    public static RowMapper<CourseEntity> getRowMapper() {
        return (rs, rowNum) -> CourseEntity.builder()
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

    public CourseEntity(Long memberId, Course course) {
        this.memberId = memberId;
        this.courseTitle = course.getTitle();
        this.duration = course.getDuration();
        this.thumbnail = course.getThumbnail();
        this.scheduled = course.isScheduled();
        this.weeks = course.getWeeks();
        this.expectedEndDate = course.getExpectedEndDate();
        this.dailyTargetTime = course.getDailyTargetTime();
    }

    public Course toCourse(List<CourseVideo> courseVideoList) {
        return new Course(courseTitle, duration, thumbnail, scheduled, expectedEndDate, weeks,
                dailyTargetTime, startDate, courseVideoList);
    }
}
