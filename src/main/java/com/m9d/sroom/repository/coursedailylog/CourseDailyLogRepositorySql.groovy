package com.m9d.sroom.repository.coursedailylog

class CourseDailyLogRepositorySql {
    public static final String GET_DATE_GROUP_DATA_BY_MEMBER_ID = """
    SELECT course_daily_log_id, member_id, course_id, daily_log_date, SUM(learning_time) as learning_time, SUM(quiz_count) as quiz_count, SUM(lecture_count) as lecture_count
    FROM COURSE_DAILY_LOG
    WHERE member_id = ?
    GROUP BY daily_log_date
    ORDER BY daily_log_date DESC
    """
}
