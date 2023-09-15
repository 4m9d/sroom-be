package com.m9d.sroom.review.sql

class ReviewSqlQuery {

    public static final String GET_LECTURE_DATA_BY_COURSE_ID = """
    SELECT lecture_id, course_id, source_id, is_playlist, lecture_index, channel, is_reviewed
    FROM LECTURE
    WHERE course_id = ?
    ORDER BY lecture_index
    """

    public static final String GET_LECTURE_DATA_BY_ID = """
    SELECT lecture_id, course_id, source_id, is_playlist, lecture_index, channel, is_reviewed
    FROM LECTURE
    WHERE lecture_id = ?
    """

    public static final String GET_VIDEO_COUNT_BY_LECTURE_ID = """
    SELECT COUNT(*) as total_video_count, SUM(is_complete) as completed_video_count
    FROM COURSEVIDEO
    WHERE lecture_id = ?
    """

    public static final String GET_PLAYLIST_DATA_BY_SOURCE_ID = """
    SELECT thumbnail, title
    FROM PLAYLIST
    WHERE playlist_id = ?
    """

    public static final String GET_VIDEO_DATA_BY_SOURCE_ID = """    
    SELECT thumbnail, title, duration
    FROM VIDEO
    WHERE video_id = ?
    """

    public static final String GET_VIEW_DURATION_BY_LECTURE_ID = """
    SELECT max_duration
    FROM COURSEVIDEO
    WHERE lecture_id = ?
    """

    public static final String GET_REVIEW_BY_LECTURE_ID = """
    SELECT review_id, source_code, member_id, lecture_id, content, submitted_rating, submitted_date
    FROM REVIEW
    WHERE lecture_id = ?
    """

    public static final String INSERT_REVIEW = """
    INSERT INTO REVIEW (source_code, member_id, lecture_id, submitted_rating, content)
    VALUES (?, ?, ?, ?, ?)
    """

    public static final String UPDATE_IS_REVIEWED = """
    UPDATE LECTURE
    SET is_reviewed = true
    WHERE lecture_id = ?
    """
}
