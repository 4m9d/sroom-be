package com.m9d.sroom.repository.quizoption

class QuizOptionRepositorySql {

    public static final String GET_LAST_ID = """
        SELECT LAST_INSERT_ID()
    """

    public static final String SAVE = """
        INSERT
        INTO QUIZ_OPTION (quiz_id, option_text, option_index)
        VALUES (?, ?, ?)
    """

    public static final String GET_BY_ID = """
        SELECT
        quiz_option_id, quiz_id, option_text, option_index
        FROM QUIZ_OPTION
        WHERE quiz_option_id = ?
    """

    public static final String GET_LIST_BY_QUIZ_ID = """
        SELECT
        quiz_option_id, quiz_id, option_text, option_index
        FROM QUIZ_OPTION
        WHERE quiz_id = ?
        ORDER BY option_index
    """
}
