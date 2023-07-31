package com.m9d.sroom.course.sql

class CourseSqlQuery {
    public static final String GET_CHANNELS_BY_COURSE_ID_QUERY = """
    SELECT l.channel
    FROM LECTURE l
    WHERE l.course_id = ?
    """;

    public static final String GET_TOTAL_LECTURE_COUNT_BY_COURSE_ID_QUERY = """
    SELECT COUNT(*) as lecture_count
    FROM COURSEVIDEO c
    WHERE c.course_id = ?
    """

    public static final String GET_COMPLETED_LECTURE_COUNT_BY_COURSE_ID_QUERY = """
    SELECT COUNT(*) as completed_lecture_count
    FROM COURSEVIDEO cv
    WHERE cv.course_id = ? AND cv.is_complete = true
    """

    public static final String GET_COURSES_BY_MEMBER_ID_QUERY = """
    SELECT c.course_id, c.thumbnail, c.course_duration, c.last_view_time, c.course_title, c.progress
    FROM COURSE c
    WHERE c.member_id = ?
    ORDER BY c.last_view_time DESC
    """
}
