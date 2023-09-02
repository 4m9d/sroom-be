package com.m9d.sroom.material.sql

class MaterialSqlQuery {

    public static final String FIND_SUMMARY_ID_FROM_COURSE_VIDEO_QUERY = """
        SELECT summary_id 
        FROM COURSEVIDEO 
        WHERE course_id = ? 
        AND video_id = ?
    """

    public static final String GET_QUIZZES_BY_VIDEO_ID_QUERY = """
        SELECT quiz_id, type, question, subjective_answer, choice_answer
        FROM QUIZ 
        WHERE video_id = ?
    """

    public static final String GET_OPTIONS_BY_QUIZ_ID_QUERY = """
        SELECT option_text
        FROM QUIZ_OPTION
        WHERE quiz_id = ?
    """

    public static final String GET_COURSE_QUIZ_INFO_QUERY = """
        SELECT submitted_answer, is_correct, submitted_time
        FROM COURSEQUIZ
        WHERE quiz_id = ? AND video_id = ? AND course_id = ?
    """

    public static final String GET_SUMMARY_BY_ID_QUERY = """
        SELECT content, updated_time, is_modified
        FROM SUMMARY 
        WHERE summary_id = ?
    """

    public static final String FIND_SUMMARY_BY_COURSE_VIDEO_QUERY = """
        SELECT cv.summary_id, s.is_modified, s.updated_time
        FROM COURSEVIDEO cv
        INNER JOIN SUMMARY s
        ON s.summary_id = cv.summary_id
        WHERE cv.course_id = ?
        AND cv.video_id = ?
    """

    public static final String UPDATE_SUMMARY_QUERY = """
        UPDATE SUMMARY
        SET content = ?
        WHERE summary_id = ?
    """

    public static final String SAVE_SUMMARY_QUERY = """
        INSERT INTO SUMMARY (video_id, content, is_modified)
        VALUES (?, ?, ?)
    """

    public static final String UPDATE_SUMMARY_ID_QUERY = """
        UPDATE COURSEVIDEO
        SET summary_id = ?
        WHERE course_id = ?
        AND video_id = ?
    """
}
