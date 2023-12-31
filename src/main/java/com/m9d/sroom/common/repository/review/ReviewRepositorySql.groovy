package com.m9d.sroom.common.repository.review

class ReviewRepositorySql {
    public static final String GET_LAST_ID = """
        SELECT LAST_INSERT_ID()
    """

    public static final String SAVE = """
        INSERT 
        INTO REVIEW (source_code, member_id, lecture_id, submitted_rating, content)
        VALUES (?, ?, ?, ?, ?)
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

    public static final String GET_BRIEF_LIST_BY_CODE = """
        SELECT r.review_id, r.content, r.submitted_rating, m.member_name, r.submitted_date 
        FROM REVIEW r JOIN MEMBER m ON r.member_id = m.member_id 
        WHERE r.source_code = ? 
        ORDER BY r.submitted_date DESC LIMIT ? OFFSET ?
    """
}
