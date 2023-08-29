package com.m9d.sroom.material.repository;

import com.m9d.sroom.material.dto.response.Quiz;
import com.m9d.sroom.material.dto.response.Summary;
import com.m9d.sroom.material.model.CourseQuiz;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static com.m9d.sroom.material.sql.MaterialSqlQuery.*;

@Repository
public class MaterialRepository {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public MaterialRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Long findSummaryIdFromCourseVideo(Long courseId, Long videoId) {
        try {
            return jdbcTemplate.queryForObject(FIND_SUMMARY_ID_FROM_COURSE_VIDEO, Long.class, courseId, videoId);
        } catch (IncorrectResultSizeDataAccessException e) {
            return null;
        }
    }

    public List<Quiz> getQuizListByVideoId(Long videoId) {
        return jdbcTemplate.query(GET_QUIZZES_BY_VIDEO_ID,
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
        return jdbcTemplate.queryForList(GET_OPTIONS_BY_QUIZ_ID, String.class, quizId);
    }

    public Optional<CourseQuiz> findCourseQuizInfo(Long quizId, Long videoId, Long courseId) {
        try {
            CourseQuiz courseQuiz = jdbcTemplate.queryForObject(GET_COURSE_QUIZ_INFO,
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

    public Summary getSummaryById(Long summaryId) {
        return jdbcTemplate.queryForObject(GET_SUMMARY_BY_ID,
                (rs, rowNum) -> new Summary(
                        rs.getString("content"),
                        rs.getBoolean("is_original"),
                        rs.getTimestamp("updated_time")
                ), summaryId);
    }
}
