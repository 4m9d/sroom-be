package com.m9d.sroom.course;

import com.m9d.sroom.common.entity.jpa.CourseEntity;
import com.m9d.sroom.common.entity.jpa.MemberEntity;
import com.m9d.sroom.util.RepositoryTest;
import com.m9d.sroom.util.TestConstant;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Date;

public class CourseRepositoryTest extends RepositoryTest {

    @Test
    @DisplayName("코스 저장에 성공합니다.")
    void saveCourse() {
        //given
        MemberEntity member = getMemberEntity();
        int weeks = 2;

        //when
        courseRepository.save(CourseEntity.createWithoutSchedule(member, TestConstant.COURSE_TITLE,
                TestConstant.THUMBNAIL));
        courseRepository.save(CourseEntity.createWithSchedule(member, TestConstant.COURSE_TITLE, TestConstant.THUMBNAIL,
                true, weeks, new Date(), 30));

        //then
        Assertions.assertEquals(member.getCourses().size(), 2);
        Assertions.assertEquals(member.getCourses().get(1).getScheduling().getWeeks(), weeks);
    }

    @Test
    @DisplayName("course 삭제에 성공합니다.")
    void removeCourse() {
        //given
        MemberEntity member = getMemberEntity();
        CourseEntity course = getCourseEntity(member);

        //when
        courseRepository.deleteById(course.getCourseId());

        //then
        Assertions.assertTrue(member.getCourses().isEmpty());
    }
}
