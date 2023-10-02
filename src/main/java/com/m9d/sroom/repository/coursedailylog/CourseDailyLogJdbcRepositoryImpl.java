package com.m9d.sroom.repository.coursedailylog;

import com.m9d.sroom.global.mapper.CourseDailyLog;
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
    public CourseDailyLog save(CourseDailyLog dailyLog) {
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
    public CourseDailyLog getById(Long dailyLogId) {
        return jdbcTemplate.queryForObject(CourseDailyLogRepositorySql.GET_BY_ID,
                CourseDailyLog.getRowMapper(), dailyLogId);
    }

    @Override
    public Optional<CourseDailyLog> findByCourseIdAndDate(Long courseId, Date date) {
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(CourseDailyLogRepositorySql.GET_BY_COURSE_ID_AND_DATE,
                    CourseDailyLog.getRowMapper(), courseId, date));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public CourseDailyLog updateById(Long dailyLogId, CourseDailyLog dailyLog) {
        jdbcTemplate.update(CourseDailyLogRepositorySql.UPDATE_BY_ID,
                dailyLog.getLearningTime(),
                dailyLog.getQuizCount(),
                dailyLog.getLectureCount(),
                dailyLogId);
        return getById(dailyLogId);
    }

    @Override
    public void updateQuizCountByCourseIdAndDate(Long courseId, Date date, int quizCount) {

    }

    @Override
    public Integer countQuizByCourseIdAndDate(Long courseId, Date date) {
        return null;
    }

    @Override
    public List<CourseDailyLog> getDateDataByMemberId(Long memberId) {

        return jdbcTemplate.query(CourseDailyLogRepositorySql.GET_DATE_GROUP_DATA_BY_MEMBER_ID,
                CourseDailyLog.getRowMapper(), memberId);

    }
}
