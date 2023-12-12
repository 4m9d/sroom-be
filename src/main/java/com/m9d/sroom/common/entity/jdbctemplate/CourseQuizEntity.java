package com.m9d.sroom.common.entity.jdbctemplate;

import lombok.Builder;
import lombok.Data;
import org.springframework.jdbc.core.RowMapper;

import java.sql.Timestamp;

@Data
@Builder
public class CourseQuizEntity {

    private Long id;

    private Long courseId;

    private Long memberId;

    private Long courseVideoId;

    private Long quizId;

    private Long videoId;

    private String submittedAnswer;

    private Boolean correct;

    private Boolean scrapped;

    private Timestamp submittedTime;

    public static RowMapper<CourseQuizEntity> getRowMapper() {
        return (rs, rowNum) -> CourseQuizEntity.builder()
                .id(rs.getLong("course_quiz_id"))
                .courseId(rs.getLong("course_id"))
                .memberId(rs.getLong("member_id"))
                .quizId(rs.getLong("quiz_id"))
                .videoId(rs.getLong("video_id"))
                .submittedAnswer(rs.getString("submitted_answer"))
                .correct(rs.getBoolean("is_correct"))
                .scrapped(rs.getBoolean("is_scrapped"))
                .submittedTime(rs.getTimestamp("submitted_time"))
                .courseVideoId(rs.getLong("course_video_id"))
                .build();
    }
}
