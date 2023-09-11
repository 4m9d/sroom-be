package com.m9d.sroom.material.sql

class MaterialSqlQuery {

    public static final String GET_QUIZZES_BY_VIDEO_ID_QUERY = """
        SELECT quiz_id, type, question, subjective_answer, choice_answer
        FROM QUIZ 
        WHERE video_id = ?
    """

    public static final String GET_OPTIONS_BY_QUIZ_ID_QUERY = """
        SELECT quiz_option_id, option_text, option_index
        FROM QUIZ_OPTION
        WHERE quiz_id = ?
        ORDER BY option_index
    """

    public static final String GET_COURSE_QUIZ_INFO_QUERY = """
        SELECT submitted_answer, is_correct, submitted_time, is_scrapped
        FROM COURSEQUIZ
        WHERE quiz_id = ? AND course_video_id = ?
    """

    public static final String GET_SUMMARY_BY_ID_QUERY = """
        SELECT content, updated_time, is_modified
        FROM SUMMARY 
        WHERE summary_id = ?
    """

    public static final String FIND_SUMMARY_BY_COURSE_VIDEO_QUERY = """
        SELECT cv.summary_id, s.is_modified, s.updated_time, cv.course_id, cv.video_id
        FROM COURSEVIDEO cv
        INNER JOIN SUMMARY s
        ON s.summary_id = cv.summary_id
        WHERE cv.course_video_id = ?
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
        WHERE course_video_id = ?
    """

    public static final String SAVE_COURSE_QUIZ_QUERY = """
        INSERT INTO COURSEQUIZ
        (course_id, quiz_id, video_id, course_video_id, submitted_answer, is_correct)
        VALUES (?, ?, ?, ?, ?, ?)
    """

    public static final String GET_VIDEO_ID_BY_QUIZ_ID_QUERY = """
        SELECT
        video_id
        FROM QUIZ
        WHERE quiz_id = ?
    """

    public static final String GET_COURSE_QUIZ_BY_ID_QUERY = """
        SELECT course_id, quiz_id, video_id, course_video_id
        FROM COURSEQUIZ
        WHERE course_quiz_id = ?
    """

    public static final String UPDATE_COURSE_QUIZ_SCRAP_QUERY = """
        UPDATE COURSEQUIZ
        SET is_scrapped = NOT is_scrapped
        WHERE course_quiz_id = ?
    """

    public static final String GET_SCRAPPED_FLAG_QUERY = """
        SELECT is_scrapped
        FROM COURSEQUIZ
        WHERE course_quiz_id = ?
    """

    public static final String SAVE_SUBJECTIVE_QUIZ_QUERY = """
        INSERT INTO QUIZ
        (video_id, type, question, subjective_answer)
        VALUES (?, ?, ?, ?)
    """

    public static final String SAVE_MULTIPLE_CHOICE_QUIZ_QUIERY = """
        INSERT INTO QUIZ
        (video_id, type, question, choice_answer)
        VALUES (?, ?, ?, ?)
    """

    public static final String SAVE_QUIZ_OPTION_QUERY = """
        INSERT INTO QUIZ_OPTION
        (quiz_id, option_text, option_index)
        VALUES (?, ?, ?)
    """

    public static final String UPDATE_MATERIAL_STATUS_CREATING_QUERY = """
        UPDATE VIDEO
        SET material_status = ?
        WHERE video_code = ?
    """
}
