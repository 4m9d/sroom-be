package com.m9d.sroom.common.repository.coursequiz

class CourseQuizRepositorySql {

    public static final String GET_LAST_ID = """
        SELECT LAST_INSERT_ID()
    """

    public static final String SAVE = """
        INSERT
        INTO COURSEQUIZ (course_id, quiz_id, video_id, submitted_answer, is_correct, course_video_id, member_id)
        VALUES (?, ?, ?, ?, ?, ?, ?)
    """

    public static final String GET_BY_ID = """
        SELECT
        course_quiz_id, course_id, quiz_id, video_id, submitted_answer, is_correct, is_scrapped, submitted_time, 
        course_video_id, member_id
        FROM COURSEQUIZ
        WHERE course_quiz_id = ?
    """

    public static final String GET_WRONG_QUIZ_LIST_BY_MEMBER_ID = """
        SELECT
        course_quiz_id, course_id, quiz_id, video_id, submitted_answer, is_correct, is_scrapped, submitted_time, 
        course_video_id, member_id
        FROM COURSEQUIZ
        WHERE member_id = ? AND is_correct = 0
        ORDER BY submitted_time DESC
        LIMIT ?
    """

    public static final String UPDATE_BY_ID = """
        UPDATE
        COURSEQUIZ
        SET submitted_answer = ?, is_correct = ?, is_scrapped = ?
        WHERE course_quiz_id = ?
    """

    public static final String GET_BY_QUIZ_ID_AND_COURSE_VIDEO_ID = """
        SELECT
        course_quiz_id, course_id, quiz_id, video_id, submitted_answer, is_correct, is_scrapped, submitted_time, 
        course_video_id, member_id
        FROM COURSEQUIZ
        WHERE quiz_id = ? AND course_video_id = ?
    """

    public static final String DELETE_BY_COURSE_ID = """
        DELETE FROM COURSEQUIZ
        WHERE course_id = ?
    """
}