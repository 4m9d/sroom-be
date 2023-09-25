package com.m9d.sroom.repository.lecture

class LectureRepositorySql {

    public static final String GET_LAST_ID = """
        SELECT LAST_INSERT_ID()
    """

    public static final String SAVE = """
        INSERT
        INTO LECTURE (course_id, source_id, is_playlist, lecture_index, member_id, channel)
        VALUES (?, ?, ?, ?, ?, ?)
    """

    public static final String GET_BY_ID = """
        SELECT
        lecture_id, course_id, source_id, is_playlist, lecture_index, is_reviewed, member_id, channel
        FROM LECTURE
        WHERE lecture_id = ?
    """

    public static final String GET_LIST_BY_COURSE_ID = """
        SELECT
        lecture_id, course_id, source_id, is_playlist, lecture_index, is_reviewed, member_id, channel
        FROM LECTURE
        WHERE course_id = ?
    """
}
