package com.m9d.sroom.repository.coursedailylog

class CourseDailyLogRepositorySql {

    public static final String GET_LAST_ID = """
        SELECT LAST_INSERT_ID()
    """

    public static final String SAVE = """
        INSERT
        INTO COURSE_DAILY_LOG (member_id, course_id, daily_log_date, learning_time, quiz_count, lecture_count)
        VALUES (?, ?, ?, ?, ?, ?)
    """

    public static final String GET_BY_COURSE_ID_AND_DATE = """
        SELECT
        course_daily_log_id, member_id, course_id, daily_log_date, learning_time, quiz_count, lecture_count
        FROM COURSE_DAILY_LOG
        WHERE
        course_id = ? AND daily_log_date = ?
        
    """

    public static final String GET_DATE_GROUP_DATA_BY_MEMBER_ID = """
    SELECT course_daily_log_id, member_id, course_id, daily_log_date, SUM(learning_time) as learning_time, SUM(quiz_count) as quiz_count, SUM(lecture_count) as lecture_count
    FROM COURSE_DAILY_LOG
    WHERE member_id = ?
    GROUP BY daily_log_date
    ORDER BY daily_log_date DESC
    """
}
