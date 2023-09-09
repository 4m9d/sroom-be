package com.m9d.sroom.material.repository;

import com.m9d.sroom.global.model.QuizOption;
import com.m9d.sroom.material.dto.request.SubmittedQuiz;
import com.m9d.sroom.material.dto.response.Quiz;
import com.m9d.sroom.material.dto.response.SummaryBrief;
import com.m9d.sroom.material.exception.QuizNotFoundException;
import com.m9d.sroom.material.model.CourseQuizInfo;
import com.m9d.sroom.material.model.SubmittedQuizInfo;
import com.m9d.sroom.global.model.Summary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
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

    public Long findSummaryIdByCourseVideoId(Long courseVideoId) {
        try {
            return jdbcTemplate.queryForObject(FIND_SUMMARY_ID_FROM_COURSE_VIDEO_QUERY, Long.class, courseVideoId);
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

    public List<QuizOption> getQuizOptionListByQuizId(Long quizId) {
        return jdbcTemplate.query(GET_OPTIONS_BY_QUIZ_ID_QUERY, (rs, rowNum) -> QuizOption.builder()
                .quizOptionId(rs.getLong("quiz_option_id"))
                .quizId(quizId)
                .optionText(rs.getString("option_text"))
                .index(rs.getInt("option_index"))
                .build(), quizId);
    }

    public Optional<SubmittedQuizInfo> findCourseQuizInfo(Long quizId, Long courseVideoId) {
        try {
            SubmittedQuizInfo submittedQuizInfo = jdbcTemplate.queryForObject(GET_COURSE_QUIZ_INFO_QUERY,
                    (rs, rowNum) -> SubmittedQuizInfo.builder()
                            .submittedAnswer(rs.getString("submitted_answer"))
                            .correct(rs.getBoolean("is_correct"))
                            .submittedTime(rs.getTimestamp("submitted_time"))
                            .scrapped(rs.getBoolean("is_scrapped"))
                            .build(),
                    quizId, courseVideoId);
            return Optional.ofNullable(submittedQuizInfo);
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

    public Optional<Summary> findSummaryByCourseVideoId(Long courseVideoId) {
        try {
            Summary summary = jdbcTemplate.queryForObject(FIND_SUMMARY_BY_COURSE_VIDEO_QUERY,
                    (rs, rowNum) -> Summary.builder()
                            .id(rs.getLong("summary_id"))
                            .modified(rs.getBoolean("is_modified"))
                            .videoId(rs.getLong("video_id"))
                            .courseId(rs.getLong("course_id"))
                            .updatedAt(rs.getTimestamp("updated_time"))
                            .build(), courseVideoId);
            return Optional.ofNullable(summary);
        } catch (IncorrectResultSizeDataAccessException e) {
            return Optional.empty();
        }
    }

    public void updateSummary(long summaryId, String newContent) {
        jdbcTemplate.update(UPDATE_SUMMARY_QUERY, newContent, summaryId);
    }

    public void saveSummary(Long videoId, String newContent, boolean modified) {
        jdbcTemplate.update(SAVE_SUMMARY_QUERY, videoId, newContent, modified);
    }

    public void updateSummaryIdByCourseVideoId(Long courseVideoId, long summaryId) {
        jdbcTemplate.update(UPDATE_SUMMARY_ID_QUERY, summaryId, courseVideoId);
    }

    public Long saveCourseQuiz(Long courseId, Long videoId, Long courseVideoId, SubmittedQuiz submittedQuiz) {
        jdbcTemplate.update(SAVE_COURSE_QUIZ_QUERY, courseId, submittedQuiz.getQuizId(), videoId, courseVideoId, submittedQuiz.getSubmittedAnswer(), submittedQuiz.getIsCorrect());

        return jdbcTemplate.queryForObject(GET_LAST_INSERT_ID_QUERY, Long.class);
    }

    public Long getVideoIdByQuizId(Long quizId) {
        try {
            return jdbcTemplate.queryForObject(GET_VIDEO_ID_BY_QUIZ_ID_QUERY, Long.class, quizId);
        } catch (EmptyResultDataAccessException e) {
            throw new QuizNotFoundException();
        }
    }

    public Optional<CourseQuizInfo> findCourseQuizInfoById(Long courseQuizId) {
        try {
            CourseQuizInfo courseQuizInfo = jdbcTemplate.queryForObject(GET_COURSE_QUIZ_BY_ID_QUERY, (rs, rowNum) -> CourseQuizInfo.builder()
                    .courseId(rs.getLong("course_id"))
                    .videoId(rs.getLong("video_id"))
                    .quizId(rs.getLong("quiz_id"))
                    .courseVideoId(rs.getLong("course_video_id"))
                    .build(), courseQuizId);
            return Optional.ofNullable(courseQuizInfo);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public void switchQuizScrapFlag(Long courseQuizId) {
        jdbcTemplate.update(UPDATE_COURSE_QUIZ_SCRAP_QUERY, courseQuizId);
    }

    public Boolean isScrappedById(Long courseQuizId) {
        return jdbcTemplate.queryForObject(GET_SCRAPPED_FLAG_QUERY, Boolean.class, courseQuizId);
    }

    public void saveSubjectiveQuiz(Long videoId, int quizType, String question, String answer) {
        jdbcTemplate.update(SAVE_SUBJECTIVE_QUIZ_QUERY, videoId, quizType, question, answer);
    }

    public Long saveMultipleChoiceQuiz(Long videoId, int quizType, String quizQuestion, int answer) {
        jdbcTemplate.update(SAVE_MULTIPLE_CHOICE_QUIZ_QUIERY, videoId, quizType, quizQuestion, answer);

        return jdbcTemplate.queryForObject(GET_LAST_INSERT_ID_QUERY, Long.class);
    }

    public void saveQuizOption(Long quizId, String optionText, int index) {
        jdbcTemplate.update(SAVE_QUIZ_OPTION_QUERY, quizId, optionText, index);
    }

    public void updateMaterialStatusByCode(String videoCode, int statusValue) {
        jdbcTemplate.update(UPDATE_MATERIAL_STATUS_CREATING_QUERY, statusValue, videoCode);
    }

    public Long getSummaryIdByVideoId(Long videoId, boolean modified) {
        return jdbcTemplate.queryForObject(GET_SUMMARY_ID_BY_VIDEO_ID_QUERY, Long.class, videoId, modified);
    }
}
