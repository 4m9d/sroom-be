package com.m9d.sroom.common.repository.course;

import com.m9d.sroom.common.entity.CourseEntity;
import com.m9d.sroom.search.dto.response.CourseBrief;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public class CourseJdbcRepositoryImpl implements CourseRepository {

    private final JdbcTemplate jdbcTemplate;

    public CourseJdbcRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public CourseEntity save(CourseEntity course) {
        jdbcTemplate.update(CourseRepositorySql.SAVE,
                course.getMemberId(),
                course.getCourseTitle(),
                course.getDuration(),
                course.getThumbnail(),
                course.isScheduled(),
                course.getWeeks(),
                course.getExpectedEndDate(),
                course.getDailyTargetTime());
        return getById(jdbcTemplate.queryForObject(CourseRepositorySql.GET_LAST_ID, Long.class));
    }

    @Override
    public CourseEntity getById(Long courseId) {
        return jdbcTemplate.queryForObject(CourseRepositorySql.GET_BY_ID, CourseEntity.getRowMapper(), courseId);
    }

    @Override
    public Optional<CourseEntity> findById(Long courseId) {
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(CourseRepositorySql.GET_BY_ID, CourseEntity.getRowMapper(), courseId));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public CourseEntity updateById(Long courseId, CourseEntity course) {
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
    public List<CourseEntity> getLatestOrderByMemberId(Long memberId) {
        return jdbcTemplate.query(CourseRepositorySql.GET_LATEST_ORDER_BY_MEMBER_ID, CourseEntity.getRowMapper(), memberId);
    }

    @Override
    public List<CourseBrief> getBriefListByMemberId(Long memberId) {
        return jdbcTemplate.query(CourseRepositorySql.GET_BRIEF_LIST_BY_MEMBER_ID, CourseBrief.getRowMapper(), memberId);
    }
}
