package com.m9d.sroom.common.repository.coursedailylog;

import com.m9d.sroom.common.dto.CourseDailyLog;

import java.sql.Date;
import java.util.List;
import java.util.Optional;

public interface CourseDailyLogRepository {

    CourseDailyLog save(CourseDailyLog dailyLog);

    CourseDailyLog getById(Long dailyLogId);

    Optional<CourseDailyLog> findByCourseIdAndDate(Long courseId, Date date);

    CourseDailyLog updateById(Long dailyLogId, CourseDailyLog dailyLog);

    public List<CourseDailyLog> getDateDataByMemberId(Long memberId);
}
