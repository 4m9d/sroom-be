package com.m9d.sroom.repository.course

class CourseRepositorySql {

    public static final String GET_LAST_ID = """
        SELECT LAST_INSERT_ID()
    """

    public static final String SAVE = """
        INSERT
        INTO COURSE (member_id, course_title, course_duration, last_view_time, thumbnail, is_scheduled, weeks, 
        expected_end_date, daily_target_time)
        VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
    """

    public static final String GET_BY_ID = """
        SELECT
        course_id, member_id, course_title, course_duration, last_view_time, progress, thumbnail, is_scheduled, weeks,
        expected_end_date, daily_target_time, start_date
        FROM COURSE
        WHERE course_id = ?
    """

    public static final String UPDATE_BY_ID = """
        UPDATE COURSE
        SET member_id = ?, course_title = ?, course_duration = ?, last_view_time = ?, progress = ?, thumbnail = ?, 
        is_scheduled = ?, weeks = ?, expected_end_date = ?, daily_target_time = ?, start_date = ?
        WHERE course_id = ?
    """
}
