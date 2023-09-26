package com.m9d.sroom.repository.coursequiz;

import com.m9d.sroom.global.mapper.CourseQuiz;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class CourseQuizJdbcRepositoryImpl implements CourseQuizRepository {

    private final JdbcTemplate jdbcTemplate;
    public CourseQuizJdbcRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Long save(CourseQuiz courseQuiz) {
        return null;
    }

    @Override
    public void deleteByCourseId(Long courseId) {
        jdbcTemplate.update(CourseQuizRepositorySql.DELETE_BY_COURSE_ID, courseId);
    }

    @Override
    public void updateScrappedById(Long courseQuizId) {

    }

    @Override
    public Boolean isScrappedById(Long courseQuizId) {
        return null;
    }
}
