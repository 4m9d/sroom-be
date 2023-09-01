package com.m9d.sroom.material.repository;

import com.m9d.sroom.material.dto.response.Quiz;
import com.m9d.sroom.material.dto.response.SummaryBrief;
import com.m9d.sroom.material.model.CourseQuiz;
import com.m9d.sroom.material.model.Summary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static com.m9d.sroom.course.sql.CourseSqlQuery.GET_LAST_INSERT_ID_QUERY;
import static com.m9d.sroom.material.sql.MaterialSqlQuery.*;

@Repository
public class MaterialRepository {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public MaterialRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Long findSummaryIdByCourseAndVideoId(Long courseId, Long videoId) {
        try {
            return jdbcTemplate.queryForObject(FIND_SUMMARY_ID_FROM_COURSE_VIDEO_QUERY, Long.class, courseId, videoId);
        } catch (IncorrectResultSizeDataAccessException e) {
            return null;
        }
    }

    public List<Quiz> getQuizListByVideoId(Long videoId) {
        return jdbcTemplate.query(GET_QUIZZES_BY_VIDEO_ID_QUERY,
                (rs, rowNum) -> {
                    int type = rs.getInt("type");
                    String answer;

                    switch (type) {
                        case 1:
                        case 3:
                            answer = String.valueOf(rs.getInt("choice_answer"));
                            break;
                        case 2:
                            answer = rs.getString("subjective_answer");
                            break;
                        default:
                            answer = "";
                    }
                    return Quiz.builder()
                            .id(rs.getLong("quiz_id"))
                            .type(rs.getInt("type"))
                            .question(rs.getString("question"))
                            .answer(answer)
                            .build();
                }, videoId);
    }

    public List<String> getQuizOptionListByQuizId(Long quizId) {
        return jdbcTemplate.queryForList(GET_OPTIONS_BY_QUIZ_ID_QUERY, String.class, quizId);
    }

    public Optional<CourseQuiz> findCourseQuizInfo(Long quizId, Long videoId, Long courseId) {
        try {
            CourseQuiz courseQuiz = jdbcTemplate.queryForObject(GET_COURSE_QUIZ_INFO_QUERY,
                    (rs, rowNum) -> CourseQuiz.builder()
                            .submittedAnswer(rs.getString("submitted_answer"))
                            .correct(rs.getBoolean("is_correct"))
                            .submittedTime(rs.getTimestamp("submitted_time"))
                            .build(),
                    quizId, videoId, courseId);
            return Optional.ofNullable(courseQuiz);
        } catch (IncorrectResultSizeDataAccessException e) {
            return Optional.empty();
        }
    }

    public SummaryBrief getSummaryById(Long summaryId) {
        return jdbcTemplate.queryForObject(GET_SUMMARY_BY_ID_QUERY,
                (rs, rowNum) -> new SummaryBrief(
                        rs.getString("content"),
                        rs.getBoolean("is_modified"),
                        rs.getTimestamp("updated_time")
                ), summaryId);
    }

    public Optional<Summary> findSummaryByCourseVideo(long courseId, Long videoId) {
        try {
            Summary summary = jdbcTemplate.queryForObject(FIND_SUMMARY_BY_COURSE_VIDEO_QUERY,
                    (rs, rowNum) -> Summary.builder()
                            .id(rs.getLong("summary_id"))
                            .modified(rs.getBoolean("is_modified"))
                            .videoId(videoId)
                            .courseId(courseId)
                            .updatedAt(rs.getTimestamp("updated_time"))
                            .build(), courseId, videoId);
            return Optional.ofNullable(summary);
        } catch (IncorrectResultSizeDataAccessException e) {
            return Optional.empty();
        }
    }

    public void updateSummary(long summaryId, String newContent) {
        jdbcTemplate.update(UPDATE_SUMMARY_QUERY, newContent, summaryId);
    }

    public Long saveSummaryModified(Long videoId, String newContent) {
        jdbcTemplate.update(SAVE_SUMMARY_QUERY, videoId, newContent, true);

        return jdbcTemplate.queryForObject(GET_LAST_INSERT_ID_QUERY, Long.class);
    }

    public void updateSummaryIdByCourseVideo(Long videoId, long courseId, long summaryId) {
        jdbcTemplate.update(UPDATE_SUMMARY_ID_QUERY, summaryId, courseId, videoId);
    }
}
