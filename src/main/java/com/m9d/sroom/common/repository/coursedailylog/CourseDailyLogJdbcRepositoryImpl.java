package com.m9d.sroom.common.repository.coursedailylog;

import com.m9d.sroom.common.entity.jdbctemplate.CourseDailyLogEntity;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.util.List;
import java.util.Optional;

@Repository
public class CourseDailyLogJdbcRepositoryImpl implements CourseDailyLogRepository {

    private final JdbcTemplate jdbcTemplate;

    public CourseDailyLogJdbcRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public CourseDailyLogEntity save(CourseDailyLogEntity dailyLog) {
        jdbcTemplate.update(CourseDailyLogRepositorySql.SAVE,
                dailyLog.getMemberId(),
                dailyLog.getCourseId(),
                dailyLog.getDailyLogDate(),
                dailyLog.getLearningTime(),
                dailyLog.getQuizCount(),
                dailyLog.getLectureCount());
        return getById(jdbcTemplate.queryForObject(CourseDailyLogRepositorySql.GET_LAST_ID, Long.class));
    }

    @Override
    public CourseDailyLogEntity getById(Long dailyLogId) {
        return jdbcTemplate.queryForObject(CourseDailyLogRepositorySql.GET_BY_ID,
                CourseDailyLogEntity.getRowMapper(), dailyLogId);
    }

    @Override
    public Optional<CourseDailyLogEntity> findByCourseIdAndDate(Long courseId, Date date) {
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(CourseDailyLogRepositorySql.GET_BY_COURSE_ID_AND_DATE,
                    CourseDailyLogEntity.getRowMapper(), courseId, date));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public CourseDailyLogEntity updateById(Long dailyLogId, CourseDailyLogEntity dailyLog) {
        jdbcTemplate.update(CourseDailyLogRepositorySql.UPDATE_BY_ID,
                dailyLog.getLearningTime(),
                dailyLog.getQuizCount(),
                dailyLog.getLectureCount(),
                dailyLogId);
        return getById(dailyLogId);
    }

    @Override
    public List<CourseDailyLogEntity> getDateDataByMemberId(Long memberId) {

        return jdbcTemplate.query(CourseDailyLogRepositorySql.GET_DATE_GROUP_DATA_BY_MEMBER_ID,
                CourseDailyLogEntity.getRowMapper(), memberId);

    }
}
