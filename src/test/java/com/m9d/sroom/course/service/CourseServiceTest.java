package com.m9d.sroom.course.service;

import com.m9d.sroom.course.dto.response.CourseInfo;
import com.m9d.sroom.course.dto.response.EnrolledCourseInfo;
import com.m9d.sroom.course.dto.response.MyCourses;
import com.m9d.sroom.member.domain.Member;
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
        Member member = getNewMember();
        Long memberId = member.getMemberId();

        String courseInsertSql = "INSERT INTO COURSE(member_id, course_title, thumbnail, last_view_time, progress) values(?, ?, ?, ?, ?)";
        jdbcTemplate.update(courseInsertSql, memberId, "course1", " ", "2023-06-13", 100);
        jdbcTemplate.update(courseInsertSql, memberId, "course2", " ", "2023-04-14", 93);
        jdbcTemplate.update(courseInsertSql, memberId, "course3", " ", "2023-11-21", 40);

        String lectureInsertSql = "INSERT INTO LECTURE(course_id, channel, is_playlist) values(?, ?, ?)";
        String getCourseIdSql = "SELECT course_id FROM COURSE WHERE course_title = ?";
        String courseVideoInsertSql = "INSERT INTO COURSEVIDEO(course_id, is_complete, video_id, summary_id, video_index) values(?, ?, 0, 0, 0)";
        for (int i = 1; i <= 3; i++) {
            String courseId = jdbcTemplate.queryForObject(getCourseIdSql, String.class, "course".concat(String.valueOf(i)));
            jdbcTemplate.update(lectureInsertSql, Long.valueOf(courseId), "channel1", "false");
            jdbcTemplate.update(lectureInsertSql, Long.valueOf(courseId), "channel2", "false");
            jdbcTemplate.update(courseVideoInsertSql, Long.valueOf(courseId), true);
            jdbcTemplate.update(courseVideoInsertSql, Long.valueOf(courseId), false);
        }




        //when
        MyCourses myCourses = courseService.getMyCourses(memberId);

        //then
        System.out.println(myCourses);
    }


    @Test
    @DisplayName("신규 코스 등록에 성공합니다.")
    void createNewCourse() {
        //given
        Member member = getNewMember();

        //when
        EnrolledCourseInfo enrolledCourseInfo = courseService.enrollCourse(member.getMemberId(), VIDEO_CODE);

        //then
        Long courseIdInLectureTable = courseRepository.getCourseIdByLectureId(enrolledCourseInfo.getLectureId());
        Assertions.assertEquals(enrolledCourseInfo.getCourseId(), courseIdInLectureTable, "lecture table의 courseId와 등록된 courseId가 다릅니다.");
    }
}
