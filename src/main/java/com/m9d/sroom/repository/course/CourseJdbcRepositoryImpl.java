package com.m9d.sroom.repository.course;

import com.m9d.sroom.global.mapper.Course;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.List;

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
    @Transactional
    public void deleteById(Long courseId) {
        jdbcTemplate.update(CourseRepositorySql.DELETE_BY_ID, courseId);
    }

    @Override
    public List<Course> getByMemberId(Long memberId) {
        return jdbcTemplate.query(CourseRepositorySql.GET_BY_MEMBER_ID, (rs, rowNum) -> Course.builder()
                        .courseId(rs.getLong("course_id"))
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
                        .build()
                , memberId);
    }

    @Override
    public List<Course> getLatestOrderByMemberId(Long memberId) {
        return jdbcTemplate.query(CourseRepositorySql.GET_LATEST_ORDER_BY_MEMBER_ID, (rs, rowNum) -> Course.builder()
                        .courseId(rs.getLong("course_id"))
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
                        .build()
                , memberId);
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
