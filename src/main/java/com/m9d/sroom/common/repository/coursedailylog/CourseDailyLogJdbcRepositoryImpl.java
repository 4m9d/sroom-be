package com.m9d.sroom.common.repository.coursedailylog;

import com.m9d.sroom.common.dto.CourseDailyLogDto;
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
    public CourseDailyLogDto save(CourseDailyLogDto dailyLog) {
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
    public CourseDailyLogDto getById(Long dailyLogId) {
        return jdbcTemplate.queryForObject(CourseDailyLogRepositorySql.GET_BY_ID,
                CourseDailyLogDto.getRowMapper(), dailyLogId);
    }

    @Override
    public Optional<CourseDailyLogDto> findByCourseIdAndDate(Long courseId, Date date) {
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(CourseDailyLogRepositorySql.GET_BY_COURSE_ID_AND_DATE,
                    CourseDailyLogDto.getRowMapper(), courseId, date));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public CourseDailyLogDto updateById(Long dailyLogId, CourseDailyLogDto dailyLog) {
        jdbcTemplate.update(CourseDailyLogRepositorySql.UPDATE_BY_ID,
                dailyLog.getLearningTime(),
                dailyLog.getQuizCount(),
                dailyLog.getLectureCount(),
                dailyLogId);
        return getById(dailyLogId);
    }

    @Override
    public List<CourseDailyLogDto> getDateDataByMemberId(Long memberId) {

        return jdbcTemplate.query(CourseDailyLogRepositorySql.GET_DATE_GROUP_DATA_BY_MEMBER_ID,
                CourseDailyLogDto.getRowMapper(), memberId);

    }
}
