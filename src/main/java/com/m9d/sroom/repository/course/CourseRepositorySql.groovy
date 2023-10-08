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

    public static final String DELETE_BY_ID = """
        DELETE FROM COURSE
        WHERE course_id = ?
    """

    public static final String GET_BY_MEMBER_ID = """
        SELECT
        course_id, member_id, course_title, course_duration, last_view_time, progress, thumbnail, is_scheduled, weeks,
        expected_end_date, daily_target_time, start_date
        FROM COURSE
        WHERE member_id = ?
    """

    public static final String GET_LATEST_ORDER_BY_MEMBER_ID = """
        SELECT 
        course_id, member_id, course_title, course_duration, last_view_time, progress, thumbnail, is_scheduled, weeks,
        expected_end_date, daily_target_time, start_date
        FROM COURSE 
        WHERE member_id = ?
        ORDER BY 
        CASE
            WHEN progress = 100 THEN 1
            ELSE 0
        END ASC, last_view_time DESC
    """
    public static final String GET_BRIEF_LIST_BY_MEMBER_ID = """
        SELECT c.course_title, c.course_id, COUNT(cv.course_id) as video_count
        FROM COURSE c
        LEFT JOIN COURSEVIDEO cv ON c.course_id = cv.course_id
        WHERE c.member_id = ?
        GROUP BY c.course_id;
    """
}
