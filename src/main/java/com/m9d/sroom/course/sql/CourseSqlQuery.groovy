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
}
