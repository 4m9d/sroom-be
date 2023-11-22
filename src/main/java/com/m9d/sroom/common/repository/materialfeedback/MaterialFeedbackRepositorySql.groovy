package com.m9d.sroom.common.repository.materialfeedback

class MaterialFeedbackRepositorySql {

    public static final String GET_LAST_ID = """
        SELECT LAST_INSERT_ID()
    """

    public static final String GET_BY_ID = """
        SELECT
        feedback_id, member_id, content_id, content_type, rating
        FROM MATERIAL_FEEDBACK
        WHERE feedback_id = ?
    """

    public static final String SAVE = """
        INSERT
        INTO MATERIAL_FEEDBACK (member_id, content_id, content_type, rating)
        VALUES (?, ?, ?, ?)
    """

    public static final String GET_BY_MEMBER_ID = """
        SELECT
        feedback_id, member_id, content_id, content_type, rating
        FROM MATERIAL_FEEDBACK
        WHERE member_id = ?
    """

    public static final String GET_BY_MEMBER_ID_AND_TYPE_AND_MATERIAL_ID = """
        SELECT
        feedback_id, member_id, content_id, content_type, rating
        FROM MATERIAL_FEEDBACK
        WHERE member_id = ?
        AND content_type = ?
        AND content_id = ?
    """
}
