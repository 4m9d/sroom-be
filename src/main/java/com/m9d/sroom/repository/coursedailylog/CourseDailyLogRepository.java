package com.m9d.sroom.repository.coursedailylog;

import com.m9d.sroom.dashboard.dto.response.DashboardMemberData;
import com.m9d.sroom.dashboard.dto.response.LearningHistory;
import com.m9d.sroom.global.model.CourseDailyLog;

import java.sql.Date;
import java.util.List;
import java.util.Optional;

public interface CourseDailyLogRepository {

    void save(CourseDailyLog dailyLog);

    Optional<CourseDailyLog> findByCourseIdAndDate(Long courseId, Date date);

    void update(CourseDailyLog dailyLog);

    void updateQuizCountByCourseIdAndDate(Long courseId, Date date, int quizCount);

    Integer countQuizByCourseIdAndDate(Long courseId, Date date);

    List<LearningHistory> getLearningHistoryListByMemberId(Long memberId);
}
