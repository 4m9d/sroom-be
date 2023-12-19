package com.m9d.sroom.member;

import com.m9d.sroom.common.entity.jpa.CourseDailyLogEntity;
import com.m9d.sroom.common.entity.jpa.CourseEntity;
import com.m9d.sroom.common.entity.jpa.MemberEntity;
import com.m9d.sroom.util.RepositoryTest;
import com.m9d.sroom.util.TestConstant;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Date;

public class MemberEntityTest extends RepositoryTest {

    @Test
    @DisplayName("해당 멤버의 모든 로그정보를 불러옵니다.")
    void getLogs() {
        //given
        MemberEntity member = getMemberEntity();
        CourseEntity course = getCourseEntity(member);

        //when
        courseDailyLogRepository.save(CourseDailyLogEntity.builder()
                .member(member)
                .course(course)
                .learningTime(TestConstant.LOG_LEARNING_TIME)
                .quizCount(TestConstant.LOG_QUIZ_COUNT)
                .lectureCount(TestConstant.LOG_LECTURE_COUNT)
                .build());

        //then
        Assertions.assertEquals(member.getLogList().size(), 1);
        Assertions.assertEquals(member.getLogList().get(0).getCourse(), course);
    }

    @Test
    @DisplayName("해당 멤버의 모든 코스를 최신순으로 불러옵니다.")
    void getCourses() {
        //given
        MemberEntity member = getMemberEntity();

        //when
        CourseEntity course1 = courseRepository.save(CourseEntity.createWithoutSchedule(
                member, TestConstant.COURSE_TITLE, TestConstant.THUMBNAIL));
        CourseEntity course2 = courseRepository.save(CourseEntity.createWithSchedule(member, TestConstant.COURSE_TITLE, TestConstant.THUMBNAIL,
                true, 2, new Date(), 30));

        //then
        Assertions.assertEquals(member.getCoursesByLatestOrder().size(), 2);
        Assertions.assertEquals(member.getCoursesByLatestOrder().get(0), course2);
    }
}
