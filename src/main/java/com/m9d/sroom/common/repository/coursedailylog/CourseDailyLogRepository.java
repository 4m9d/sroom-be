package com.m9d.sroom.common.repository.coursedailylog;

import com.m9d.sroom.common.entity.jdbctemplate.CourseDailyLogEntity;

import java.sql.Date;
import java.util.List;
import java.util.Optional;

public interface CourseDailyLogRepository {

    CourseDailyLogEntity save(CourseDailyLogEntity dailyLog);

    CourseDailyLogEntity getById(Long dailyLogId);

    Optional<CourseDailyLogEntity> findByCourseIdAndDate(Long courseId, Date date);

    CourseDailyLogEntity updateById(Long dailyLogId, CourseDailyLogEntity dailyLog);

    public List<CourseDailyLogEntity> getDateDataByMemberId(Long memberId);
}
