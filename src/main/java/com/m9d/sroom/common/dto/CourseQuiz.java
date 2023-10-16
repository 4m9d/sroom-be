package com.m9d.sroom.common.dto;

import lombok.Builder;
import lombok.Data;
import org.springframework.jdbc.core.RowMapper;

import java.sql.Timestamp;

@Data
@Builder
public class CourseQuiz {

    private Long id;

    private Long courseId;

    private Long courseVideoId;

    private Long quizId;

    private Long videoId;

    private String submittedAnswer;

    private Boolean correct;

    private Boolean scrapped;

    private Timestamp submittedTime;

    public static RowMapper<CourseQuiz> getRowMapper() {
        return (rs, rowNum) -> CourseQuiz.builder()
                .id(rs.getLong("course_quiz_id"))
                .courseId(rs.getLong("course_id"))
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
