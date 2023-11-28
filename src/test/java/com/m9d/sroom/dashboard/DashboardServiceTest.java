package com.m9d.sroom.dashboard;


import com.m9d.sroom.dashboard.dto.response.Dashboard;
import com.m9d.sroom.common.entity.MemberEntity;
import com.m9d.sroom.util.ServiceTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class DashboardServiceTest extends ServiceTest {

    @Test
    @DisplayName("유저의 대시보드 정보를 불러옵니다")
    void getDashboardTest() {
        //given
        MemberEntity member = getNewMember();
        Long memberId = member.getMemberId();

        int total_correct_count = 1;
        int total_solved_count = 2;
        int completion_rate = 23;
        int total_learning_time = 100;

        int expect_correctness_rate = 50;

        String memberInsertSql = "UPDATE MEMBER SET (total_solved_count, total_correct_count, completion_rate, total_learning_time) = values(?, ?, ?, ?)";
        String courseInsertSql = "INSERT INTO COURSE(member_id, course_title, thumbnail, last_view_time, course_duration) values(?, ?, ?, ?, ?)";
        String dailyLogInsertSql = "INSERT INTO COURSE_DAILY_LOG(member_id, course_id, daily_log_date, quiz_count) values(?, ?, ?, ?)";
        String lectureInsertSql = "INSERT INTO LECTURE(course_id, channel, is_playlist)values(?, ?, false)";
        String courseVideoInsertSql = "INSERT INTO COURSEVIDEO(course_id, is_complete, video_id, summary_id, video_index) values(?, ?, 0, 0, 0)";

        jdbcTemplate.update(memberInsertSql, total_solved_count, total_correct_count, completion_rate, total_learning_time);

        jdbcTemplate.update(courseInsertSql, memberId, "course1", " ", "2023-05-01 10:00:00", 600);
        jdbcTemplate.update(courseInsertSql, memberId, "course2", " ", "2023-05-10 10:00:00", 3600);
        jdbcTemplate.update(courseInsertSql, memberId, "course3", " ", "2023-04-20 10:00:00", 10);

        jdbcTemplate.update(lectureInsertSql, 1, "채널 1");
        jdbcTemplate.update(lectureInsertSql, 1, "채널 1");
        jdbcTemplate.update(lectureInsertSql, 1, "채널 2");

        jdbcTemplate.update(courseVideoInsertSql, 1, true);
        jdbcTemplate.update(courseVideoInsertSql, 1, false);

        jdbcTemplate.update(dailyLogInsertSql, memberId, 1, "2023-07-28", 2);
        jdbcTemplate.update(dailyLogInsertSql, memberId, 1, "2023-07-27", 1);
        jdbcTemplate.update(dailyLogInsertSql, memberId, 2, "2023-07-26", 3);

        //when
        Dashboard dashboardInfo = dashboardService.getDashboard(memberId);

        //then
        Assertions.assertEquals(expect_correctness_rate, dashboardInfo.getCorrectnessRate());
        Assertions.assertEquals(completion_rate, dashboardInfo.getCompletionRate());
        Assertions.assertEquals(total_learning_time, dashboardInfo.getTotalLearningTime());

        Assertions.assertEquals("2023-05-10 10:00:00", dashboardInfo.getLatestLectures().get(0).getLastViewTime());
        Assertions.assertEquals("2023-05-01 10:00:00", dashboardInfo.getLatestLectures().get(1).getLastViewTime());

        Assertions.assertEquals(3, dashboardInfo.getLearningHistories().size());

        Assertions.assertNotNull(dashboardInfo.getMotivation());
    }
}
