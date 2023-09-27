package com.m9d.sroom.repository.review

class ReviewRepositorySql {
    public static final String GET_LAST_ID = """
        SELECT LAST_INSERT_ID()
    """

    public static final String SAVE = """
        INSERT 
        INTO REVIEW (source_code, member_id, lecture_id, submitted_rating, content, submitted_date)
        VALUES (?, ?, ?, ?, ?, ?)
    """

    public static final String GET_BY_ID = """
        SELECT review_id, source_code, member_id, lecture_id, submitted_rating, content, submitted_date
        FROM REVIEW
        WHERE review_id = ?
    """

    public static final String GET_BY_LECTURE_ID = """
        SELECT review_id, source_code, member_id, lecture_id, submitted_rating, content, submitted_date
        FROM REVIEW
        WHERE lecture_id = ?
    """
}
