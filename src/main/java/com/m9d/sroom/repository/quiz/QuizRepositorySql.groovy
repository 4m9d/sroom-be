package com.m9d.sroom.repository.quiz

class QuizRepositorySql {

    public static final String GET_LAST_ID = """
        SELECT LAST_INSERT_ID()
    """

    public static final String SAVE = """
        INSERT
        INTO QUIZ (video_id, type, question, subjective_answer, choice_answer)
        VALUES (?, ?, ?, ?, ?)
    """

    public static final String GET_LIST_BY_VIDEO_ID = """
        SELECT
        quiz_id, video_id, type, question, subjective_answer, choice_answer
        FROM QUIZ
        WHERE video_id = ?
    """

    public static final String GET_BY_ID = """
        SELECT
        quiz_id, video_id, type, question, subjective_answer, choice_answer
        FROM QUIZ
        WHERE quiz_id = ?
    """
}
