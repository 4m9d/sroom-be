package com.m9d.sroom.repository.coursedailylog;

import com.m9d.sroom.global.mapper.CourseDailyLog;

import java.sql.Date;
import java.util.List;
import java.util.Optional;

public interface CourseDailyLogRepository {

    void save(CourseDailyLog dailyLog);

    Optional<CourseDailyLog> findByCourseIdAndDate(Long courseId, Date date);

    void update(CourseDailyLog dailyLog);



    void updateQuizCountByCourseIdAndDate(Long courseId, Date date, int quizCount);

    Integer countQuizByCourseIdAndDate(Long courseId, Date date);

    public List<CourseDailyLog> getDateDataByMemberId(Long memberId);
}
