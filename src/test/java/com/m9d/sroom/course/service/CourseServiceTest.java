package com.m9d.sroom.course.service;

import com.m9d.sroom.course.dto.response.CourseInfo;
import com.m9d.sroom.course.repository.CourseRepository;
import com.m9d.sroom.member.dto.response.Login;
import com.m9d.sroom.util.ServiceTest;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.relational.core.sql.In;

import java.util.List;

public class CourseServiceTest extends ServiceTest {

    @Autowired
    private CourseService courseService;

    @Test
    @DisplayName("유저가 등록한 강의 코스들의 정보를 불러옵니다.")
    void getCourseListTest() {
        //given
        Long memberId = 1L;

        String courseInsertSql = "INSERT INTO COURSE(member_id, course_title) values(?, ?)";
        jdbcTemplate.update(courseInsertSql, memberId, "course1");
        jdbcTemplate.update(courseInsertSql, memberId, "course2");
        jdbcTemplate.update(courseInsertSql, memberId, "course3");

        String lectureInsertSql = "INSERT INTO LECTURE(course_id, channel) values(?, ?)";
        String getCourseIdSql = "SELECT course_id FROM COURSE WHERE course_title = ?";
        for(int i=0; i<=2; i++) {
            int courseId = Integer.valueOf(jdbcTemplate.queryForObject(getCourseIdSql, String.class, "course"+String.valueOf(i)));
            jdbcTemplate.update(lectureInsertSql, courseId, "channel1");
            jdbcTemplate.update(lectureInsertSql, courseId, "channel2");
        }

        //when
        List<CourseInfo> courseList = courseService.getCourseList(memberId);

        //then
        Assertions.assertEquals(3, courseList.size());
        Assertions.assertEquals(2, courseList.get(0).getChannels().length);
    }
}
