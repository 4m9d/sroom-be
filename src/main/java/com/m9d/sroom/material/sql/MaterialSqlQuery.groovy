package com.m9d.sroom.material.sql

class MaterialSqlQuery {

    public static final String FIND_SUMMARY_ID_FROM_COURSE_VIDEO = """
        SELECT summary_id 
        FROM COURSEVIDEO 
        WHERE course_id = ? 
        AND video_id = ?
    """

    public static final String GET_QUIZZES_BY_VIDEO_ID = """
        SELECT quiz_id, type, question, answer
        FROM QUIZ 
        WHERE video_id = ?
    """

    public static final String GET_OPTIONS_BY_QUIZ_ID = """
        SELECT option_text
        FROM QUIZ_OPTION
        WHERE quiz_id = ?
    """

    public static final String GET_COURSE_QUIZ_INFO = """
        SELECT submitted_answer, is_correct, submitted_time
        FROM COURSEQUIZ
        WHERE quiz_id = ? AND video_id = ? AND course_id = ?
    """

    public static final String GET_SUMMARY_BY_ID = """
        SELECT content, updated_time, is_original
        FROM SUMMARY 
        WHERE summary_id = ?
    """
}
