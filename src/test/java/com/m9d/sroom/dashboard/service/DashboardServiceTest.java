package com.m9d.sroom.dashboard.service;


import com.google.common.base.Equivalence;
import com.m9d.sroom.dashbord.dto.response.DashboardInfo;
import com.m9d.sroom.member.domain.Member;
import com.m9d.sroom.util.ServiceTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
public class DashboardServiceTest extends ServiceTest {

    @Test
    @DisplayName("??")
    void getDashboardTest() {
        //given
        Member member = getNewMember();
        Long memberId = member.getMemberId();
        Long courseId = 1L;

        int total_correct_count = 1;
        int total_solved_count = 1;
        float completion_rate = 23.0F;
        int total_learning_time = 100;

        float expect_correctness_rate = 100.0F;

        String memberInsertSql = "UPDATE MEMBER SET (total_solved_count, total_correct_count, completion_rate, total_learning_time) = values(?, ?, ?, ?)";
        String courseInsertSql = "INSERT INTO COURSE(member_id, course_title, thumbnail, last_view_time) values(?, ?, ?, ?)";
        String dailyLogInsertSql = "INSERT INTO COURSE_DAILY_LOG(member_id, course_id, daily_log_date) values(?, ?, ?)";

        jdbcTemplate.update(memberInsertSql, total_solved_count, total_correct_count, completion_rate, total_learning_time);

        jdbcTemplate.update(courseInsertSql, memberId, "course1", " ", "2023-05-01 10:00:00");
        jdbcTemplate.update(courseInsertSql, memberId, "course2", " ", "2023-05-10 10:00:00");
        jdbcTemplate.update(courseInsertSql, memberId, "course3", " ", "2023-04-20 10:00:00");

        jdbcTemplate.update(dailyLogInsertSql, memberId, courseId, "2023-05-01");
        jdbcTemplate.update(dailyLogInsertSql, memberId, courseId, "2023-05-10");

        //when
        DashboardInfo dashboardInfo = dashboardService.getDashboard();

        //then

        Assertions.assertEquals(expect_correctness_rate, dashboardInfo.getCorrectnessRate());
        Assertions.assertEquals(completion_rate, dashboardInfo.getCompletionRate());
        Assertions.assertEquals(total_learning_time, dashboardInfo.getTotalLearningTime());

        Assertions.assertEquals("2023-05-10 10:00:00", dashboardInfo.getLatest().get(0).getLastViewTime());
        Assertions.assertEquals("2023-05-01 10:00:00", dashboardInfo.getLatest().get(1).getLastViewTime());

        Assertions.assertEquals(2, dashboardInfo.getLearningHistory().size());
    }
}
