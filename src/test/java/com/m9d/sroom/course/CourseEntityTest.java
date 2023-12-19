package com.m9d.sroom.course;

import com.m9d.sroom.common.entity.jpa.CourseDailyLogEntity;
import com.m9d.sroom.common.entity.jpa.CourseEntity;
import com.m9d.sroom.common.entity.jpa.MemberEntity;
import com.m9d.sroom.util.RepositoryTest;
import com.m9d.sroom.util.TestConstant;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Date;

public class CourseEntityTest extends RepositoryTest {

    @Test
    @DisplayName("해당 날자의 로그 데이터를 불러옵니다.")
    void getDailyLog() {
        Date today = new Date();
        MemberEntity member = getMemberEntity();
        //given
        CourseEntity course = getCourseEntity(member);

        //when
        CourseDailyLogEntity dailyLog = getCourseDailyLogEntity(member, course);

        //then
        Assertions.assertTrue(course.findDailyLogByDate(today).isPresent());
        Assertions.assertEquals(course.findDailyLogByDate(today).get().getLearningTime(),
                dailyLog.getLearningTime());
    }
}
