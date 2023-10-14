package com.m9d.sroom.course.repository;

import com.m9d.sroom.course.CourseDto;
import com.m9d.sroom.lecture.dto.response.CourseBrief;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public class CourseJdbcRepositoryImpl implements CourseRepository {

    private final JdbcTemplate jdbcTemplate;

    public CourseJdbcRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public CourseDto save(CourseDto courseDto) {
        jdbcTemplate.update(CourseRepositorySql.SAVE,
                courseDto.getMemberId(),
                courseDto.getCourseTitle(),
                courseDto.getDuration(),
                courseDto.getLastViewTime(),
                courseDto.getThumbnail(),
                courseDto.isScheduled(),
                courseDto.getWeeks(),
                courseDto.getExpectedEndDate(),
                courseDto.getDailyTargetTime());
        return getById(jdbcTemplate.queryForObject(CourseRepositorySql.GET_LAST_ID, Long.class));
    }

    @Override
    public CourseDto getById(Long courseId) {
        return jdbcTemplate.queryForObject(CourseRepositorySql.GET_BY_ID, CourseDto.getRowMapper(), courseId);
    }

    @Override
    public CourseDto updateById(Long courseId, CourseDto courseDto) {
        jdbcTemplate.update(CourseRepositorySql.UPDATE_BY_ID,
                courseDto.getMemberId(),
                courseDto.getCourseTitle(),
                courseDto.getDuration(),
                courseDto.getLastViewTime(),
                courseDto.getProgress(),
                courseDto.getThumbnail(),
                courseDto.isScheduled(),
                courseDto.getWeeks(),
                courseDto.getExpectedEndDate(),
                courseDto.getDailyTargetTime(),
                courseDto.getStartDate(),
                courseDto.getCourseId());
        return getById(courseId);
    }

    @Override
    @Transactional
    public void deleteById(Long courseId) {
        jdbcTemplate.update(CourseRepositorySql.DELETE_BY_ID, courseId);
    }

    @Override
    public List<CourseDto> getLatestOrderByMemberId(Long memberId) {
        return jdbcTemplate.query(CourseRepositorySql.GET_LATEST_ORDER_BY_MEMBER_ID, CourseDto.getRowMapper(), memberId);
    }

    @Override
    public List<CourseBrief> getBriefListByMemberId(Long memberId) {
        return jdbcTemplate.query(CourseRepositorySql.GET_BRIEF_LIST_BY_MEMBER_ID, CourseBrief.getRowMapper(), memberId);
    }
}
