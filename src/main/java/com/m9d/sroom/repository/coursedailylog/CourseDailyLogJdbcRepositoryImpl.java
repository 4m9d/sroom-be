package com.m9d.sroom.repository.coursedailylog;

import com.m9d.sroom.dashboard.dto.response.LearningHistory;
import com.m9d.sroom.global.model.CourseDailyLog;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.util.List;
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

    @Override
    public List<LearningHistory> getLearningHistoryListByMemberId(Long memberId) {
        return null;
    }
}
