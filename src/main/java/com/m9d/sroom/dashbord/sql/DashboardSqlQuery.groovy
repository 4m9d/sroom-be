package com.m9d.sroom.dashbord.sql;

class DashboardSqlQuery {

    public static final String GET_LATEST_COURSES_SQL = """
    SELECT c.course_id, c.thumbnail, c.course_duration, c.last_view_time, c.course_title, c.progress
    FROM COURSE c
    WHERE c.member_id = ?
    ORDER BY c.last_view_time DESC LIMIT 2
    """

    public static final String GET_COURSE_DAILY_LOGS_SQL = """
    SELECT l.daily_log_date, SUM(l.learning_time) as learning_time, SUM(l.quiz_count) as quiz_count, SUM(l.lecture_count) as lecture_count
    FROM COURSE_DAILY_LOG l
    WHERE member_id = ?
    GROUP BY l.daily_log_date
    ORDER BY l.daily_log_date DESC
    """

    public static final String GET_DASHBOARD_MEMBER_DATA_SQL = """
    SELECT m.total_solved_count, m.total_correct_count, m.completion_rate, m.total_learning_time
    FROM MEMBER m
    WHERE m.member_id = ?
    """
}
