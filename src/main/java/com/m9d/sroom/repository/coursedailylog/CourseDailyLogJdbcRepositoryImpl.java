package com.m9d.sroom.repository.coursedailylog;

import com.m9d.sroom.global.mapper.CourseDailyLog;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.util.Optional;

@Repository
public class CourseDailyLogJdbcRepositoryImpl implements CourseDailyLogRepository{
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
}
