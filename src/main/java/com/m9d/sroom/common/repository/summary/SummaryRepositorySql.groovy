package com.m9d.sroom.common.repository.summary

class SummaryRepositorySql {

    public static final String GET_LAST_ID = """
        SELECT LAST_INSERT_ID()
    """

    public static final String SAVE = """
        INSERT
        INTO SUMMARY (video_id, content, is_modified)
        VALUES (?, ?, ?)
    """

    public static final String GET_BY_ID = """
        SELECT
        summary_id, video_id, content, updated_time, is_modified
        FROM SUMMARY
        WHERE summary_id = ?
    """
    public static final String UPDATE_BY_ID = """
        UPDATE
        SUMMARY SET content = ?, is_modified = ?
        WHERE summary_id = ?
    """
}
