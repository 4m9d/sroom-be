package com.m9d.sroom.course.service;

import com.m9d.sroom.course.dto.response.CourseInfo;
import com.m9d.sroom.util.ServiceTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
public class CourseServiceTest extends ServiceTest {

    @Test
    @DisplayName("유저가 등록한 강의 코스들의 정보를 불러옵니다.")
    void getCourseListTest() {
        //given
        Long memberId = 1L;

        String courseInsertSql = "INSERT INTO COURSE(member_id, course_title, thumbnail) values(?, ?, ?)";
        jdbcTemplate.update(courseInsertSql, memberId, "course1", " ");
        jdbcTemplate.update(courseInsertSql, memberId, "course2", " ");
        jdbcTemplate.update(courseInsertSql, memberId, "course3", " ");

        String lectureInsertSql = "INSERT INTO LECTURE(course_id, channel, is_playlist) values(?, ?, ?)";
        String getCourseIdSql = "SELECT course_id FROM COURSE WHERE course_title = ?";
        for(int i=1; i<=3; i++) {
            String courseId = jdbcTemplate.queryForObject(getCourseIdSql, String.class, "course".concat(String.valueOf(i)));
            jdbcTemplate.update(lectureInsertSql, Integer.valueOf(courseId), "channel1", "false");
            jdbcTemplate.update(lectureInsertSql, Integer.valueOf(courseId), "channel2", "false");
        }

        //when
        List<CourseInfo> courseList = courseService.getCourseList(memberId);

        //then
        Assertions.assertEquals(3, courseList.size());
        Assertions.assertEquals(2, courseList.get(0).getChannels().length);
    }
}
