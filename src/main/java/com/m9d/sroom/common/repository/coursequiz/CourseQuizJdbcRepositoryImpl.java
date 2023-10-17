package com.m9d.sroom.common.repository.coursequiz;

import com.m9d.sroom.common.entity.CourseQuizEntity;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class CourseQuizJdbcRepositoryImpl implements CourseQuizRepository {

    private final JdbcTemplate jdbcTemplate;

    public CourseQuizJdbcRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public CourseQuizEntity save(CourseQuizEntity courseQuiz) {
        jdbcTemplate.update(CourseQuizRepositorySql.SAVE,
                courseQuiz.getCourseId(),
                courseQuiz.getQuizId(),
                courseQuiz.getVideoId(),
                courseQuiz.getSubmittedAnswer(),
                courseQuiz.getCorrect(),
                courseQuiz.getCourseVideoId());
        return getById(jdbcTemplate.queryForObject(CourseQuizRepositorySql.GET_LAST_ID, Long.class));
    }

    @Override
    public CourseQuizEntity getById(Long courseQuizId) {
        return jdbcTemplate.queryForObject(CourseQuizRepositorySql.GET_BY_ID, CourseQuizEntity.getRowMapper(), courseQuizId);
    }

    @Override
    public Optional<CourseQuizEntity> findById(Long courseQuizId) {
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(CourseQuizRepositorySql.GET_BY_ID, CourseQuizEntity.getRowMapper(), courseQuizId));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public CourseQuizEntity updateById(Long courseQuizId, CourseQuizEntity courseQuiz) {
        jdbcTemplate.update(CourseQuizRepositorySql.UPDATE_BY_ID,
                courseQuiz.getSubmittedAnswer(),
                courseQuiz.getCorrect(),
                courseQuiz.getScrapped(),
                courseQuizId);
        return getById(courseQuizId);
    }

    @Override
    public Optional<CourseQuizEntity> findByQuizIdAndCourseVideoId(Long quizId, Long courseVideoId) {
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(
                    CourseQuizRepositorySql.GET_BY_QUIZ_ID_AND_COURSE_VIDEO_ID,
                    CourseQuizEntity.getRowMapper(), quizId, courseVideoId));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public void deleteByCourseId(Long courseId) {
        jdbcTemplate.update(CourseQuizRepositorySql.DELETE_BY_COURSE_ID, courseId);
    }
}
