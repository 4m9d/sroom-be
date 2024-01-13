package com.m9d.sroom.common;

import com.m9d.sroom.common.entity.jpa.CourseDailyLogEntity;
import com.m9d.sroom.common.entity.jpa.CourseEntity;
import com.m9d.sroom.common.entity.jpa.MemberEntity;
import com.m9d.sroom.common.repository.coursedailylog.CourseDailyLogJpaRepository;
import com.m9d.sroom.search.dto.VideoCompletionStatus;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

@Service
public class LearningActivityUpdaterVJpa {

    private final CourseDailyLogJpaRepository dailyLogRepository;

    public LearningActivityUpdaterVJpa(CourseDailyLogJpaRepository dailyLogRepository) {
        this.dailyLogRepository = dailyLogRepository;
    }

    public void updateCourseDailyLog(CourseEntity courseEntity, VideoCompletionStatus status) {
        Optional<CourseDailyLogEntity> dailyLogEntityOptional = courseEntity.findDailyLogByDate(new Date());
        int learningTimeToAdd = Math.max(status.getTimeGap(), 0);
        int lectureCountToAdd = status.isCompletedNow() ? 1 : 0;

        if (dailyLogEntityOptional.isEmpty()) {
            dailyLogRepository.save(CourseDailyLogEntity.create(courseEntity, learningTimeToAdd, 0,
                    lectureCountToAdd));
        } else {
            dailyLogEntityOptional.get().addLearningTime(learningTimeToAdd);
            dailyLogEntityOptional.get().addLectureCount(lectureCountToAdd);
        }
    }

    public void updateDailyQuizCount(CourseEntity courseEntity, int submittedQuizCount){
        Optional<CourseDailyLogEntity> dailyLogOptional = courseEntity.findDailyLogByDate(new Date());
        if(dailyLogOptional.isEmpty()){
            dailyLogRepository.save(CourseDailyLogEntity.create(courseEntity, 0, submittedQuizCount,
                    0));
        }else{
            CourseDailyLogEntity dailyLog = dailyLogOptional.get();
            dailyLog.addQuizCount(submittedQuizCount);
        }
    }

}
