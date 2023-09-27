package com.m9d.sroom.repository.coursedailylog;

import com.m9d.sroom.dashboard.dto.response.LearningHistory;
import com.m9d.sroom.dashboard.sql.DashboardSqlQuery;
import com.m9d.sroom.global.mapper.CourseDailyLog;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Optional;

@Repository
public class CourseDailyLogJdbcRepositoryImpl implements CourseDailyLogRepository{

    private final JdbcTemplate jdbcTemplate;
    public CourseDailyLogJdbcRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void save(CourseDailyLog dailyLog) {

    }

    @Override
    public Optional<CourseDailyLog> findByCourseIdAndDate(Long courseId, Date date) {
        return Optional.empty();
    }

    @Override
    public void update(CourseDailyLog dailyLog) {

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
