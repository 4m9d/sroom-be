package com.m9d.sroom.repository.coursequiz;

import com.m9d.sroom.global.mapper.CourseQuizDto;
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
    public CourseQuizDto save(CourseQuizDto courseQuizDto) {
        jdbcTemplate.update(CourseQuizRepositorySql.SAVE,
                courseQuizDto.getCourseId(),
                courseQuizDto.getQuizId(),
                courseQuizDto.getVideoId(),
                courseQuizDto.getSubmittedAnswer(),
                courseQuizDto.getCorrect(),
                courseQuizDto.getCourseVideoId());
        return getById(jdbcTemplate.queryForObject(CourseQuizRepositorySql.GET_LAST_ID, Long.class));
    }

    @Override
    public CourseQuizDto getById(Long courseQuizId) {
        return jdbcTemplate.queryForObject(CourseQuizRepositorySql.GET_BY_ID, CourseQuizDto.getRowMapper(), courseQuizId);
    }

    @Override
    public Optional<CourseQuizDto> findById(Long courseQuizId) {
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(CourseQuizRepositorySql.GET_BY_ID, CourseQuizDto.getRowMapper(), courseQuizId));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public CourseQuizDto updateById(Long courseQuizId, CourseQuizDto courseQuizDto) {
        jdbcTemplate.update(CourseQuizRepositorySql.UPDATE_BY_ID,
                courseQuizDto.getSubmittedAnswer(),
                courseQuizDto.getCorrect(),
                courseQuizDto.getScrapped(),
                courseQuizId);
        return getById(courseQuizId);
    }

    @Override
    public Optional<CourseQuizDto> findByQuizIdAndCourseVideoId(Long quizId, Long courseVideoId) {
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(
                    CourseQuizRepositorySql.GET_BY_QUIZ_ID_AND_COURSE_VIDEO_ID,
                    CourseQuizDto.getRowMapper(), quizId, courseVideoId));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public void deleteByCourseId(Long courseId) {
        jdbcTemplate.update(CourseQuizRepositorySql.DELETE_BY_COURSE_ID, courseId);
    }
}
