package com.m9d.sroom.common.repository.coursedailylog;

import com.m9d.sroom.common.dto.CourseDailyLogDto;

import java.sql.Date;
import java.util.List;
import java.util.Optional;

public interface CourseDailyLogRepository {

    CourseDailyLogDto save(CourseDailyLogDto dailyLog);

    CourseDailyLogDto getById(Long dailyLogId);

    Optional<CourseDailyLogDto> findByCourseIdAndDate(Long courseId, Date date);

    CourseDailyLogDto updateById(Long dailyLogId, CourseDailyLogDto dailyLog);

    public List<CourseDailyLogDto> getDateDataByMemberId(Long memberId);
}
