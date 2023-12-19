package com.m9d.sroom.courseDailyLog;

import com.m9d.sroom.common.entity.jpa.CourseDailyLogEntity;
import com.m9d.sroom.common.entity.jpa.CourseEntity;
import com.m9d.sroom.common.entity.jpa.MemberEntity;
import com.m9d.sroom.common.repository.coursedailylog.CourseDailyLogJpaRepository;
import com.m9d.sroom.util.RepositoryTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class CourseDailyLogRepositoryTest extends RepositoryTest {

    @Autowired
    private CourseDailyLogJpaRepository dailyLogRepository;

    @Test
    @DisplayName("로그가 저장 됩니다.")
    void saveCourseDailyLogSuccessfully() {
        //given
        int learningTime = 100;
        MemberEntity member = getMemberEntity();
        CourseEntity course = getCourseEntity(member);
        CourseDailyLogEntity dailyLog = CourseDailyLogEntity.builder()
                .member(member)
                .course(course)
                .learningTime(learningTime)
                .quizCount(2)
                .lectureCount(1)
                .build();

        //when
        dailyLogRepository.save(dailyLog);

        //then
        Assertions.assertEquals(dailyLogRepository.getById(1L).getLearningTime(), learningTime);
    }
}
