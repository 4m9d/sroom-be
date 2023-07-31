package com.m9d.sroom.dashbord.repository;

import com.m9d.sroom.course.dto.response.CourseInfo;
import com.m9d.sroom.dashbord.dto.response.DashboardMemberData;
import com.m9d.sroom.dashbord.dto.response.LearningHistory;
import com.m9d.sroom.dashbord.sql.DashboardSqlQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.text.SimpleDateFormat;
import java.util.List;

@Repository
public class DashboardRepository {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public DashboardRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<CourseInfo> getLatestCourseListByMemberId(Long memberId) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

        return jdbcTemplate.query(DashboardSqlQuery.GET_LATEST_COURSES_SQL,
                (rs, rowNum) -> CourseInfo.builder()
                        .courseId(rs.getLong("course_id"))
                        .duration(rs.getInt("course_duration") / 60)
                        .thumbnail(rs.getString("thumbnail"))
                        .progress(rs.getInt("progress"))
                        .courseTitle(rs.getString("course_title"))
                        .lastViewTime(dateFormat.format(rs.getTimestamp("last_view_time")))
                        .build(),
                memberId);
    }

    public List<LearningHistory> getLearningHistoryListByMemberId(Long memberId) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        return jdbcTemplate.query(DashboardSqlQuery.GET_COURSE_DAILY_LOGS_SQL,
                (rs, rowNum) -> LearningHistory.builder()
                        .date(dateFormat.format(rs.getTimestamp("daily_log_date")))
                        .learningTime(rs.getInt("learning_time"))
                        .lectureCount(rs.getInt("lecture_count"))
                        .quizCount(rs.getInt("quiz_count"))
                        .build(),
                memberId);

    }

    public DashboardMemberData getDashboardMemberDataByMemberId(Long memberId) {

        return jdbcTemplate.queryForObject(DashboardSqlQuery.GET_DASHBOARD_MEMBER_DATA_SQL,
                (rs, rowNum) -> DashboardMemberData.builder()
                        .totalCorrectCount(rs.getInt("total_correct_count"))
                        .completionRate(rs.getInt("completion_rate"))
                        .totalSolvedCount(rs.getInt("total_solved_count"))
                        .totalLearningTime(rs.getInt("total_learning_time"))
                        .build(),
                memberId);
    }
}
