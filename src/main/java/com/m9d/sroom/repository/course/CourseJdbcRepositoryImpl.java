package com.m9d.sroom.repository.course;

import com.m9d.sroom.global.mapper.Course;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class CourseJdbcRepositoryImpl implements CourseRepository {

    private final JdbcTemplate jdbcTemplate;

    public CourseJdbcRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    @Transactional
    public Course save(Course course) {
        jdbcTemplate.update(CourseRepositorySql.SAVE,
                course.getMemberId(),
                course.getCourseTitle(),
                course.getDuration(),
                course.getLastViewTime(),
                course.getThumbnail(),
                course.isScheduled(),
                course.getWeeks(),
                course.getExpectedEndDate(),
                course.getDailyTargetTime());
        return getById(jdbcTemplate.queryForObject(CourseRepositorySql.GET_LAST_ID, Long.class));
    }

    @Override
    public Course getById(Long courseId) {
        return jdbcTemplate.queryForObject(CourseRepositorySql.GET_BY_ID, Course.getRowMapper(), courseId);
    }

    @Override
    public Course updateById(Long courseId, Course course) {
        jdbcTemplate.update(CourseRepositorySql.UPDATE_BY_ID,
                course.getMemberId(),
                course.getCourseTitle(),
                course.getDuration(),
                course.getLastViewTime(),
                course.getProgress(),
                course.getThumbnail(),
                course.isScheduled(),
                course.getWeeks(),
                course.getExpectedEndDate(),
                course.getDailyTargetTime(),
                course.getStartDate(),
                course.getCourseId());
        return getById(courseId);
    }

    @Override
    public void deleteById(Long courseId) {
    }

    @Override
    public Integer countByMemberId(Long memberId) {
        return null;
    }

    @Override
    public Integer countCompletedByMemberId(Long memberId) {
        return null;
    }
}
