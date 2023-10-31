package com.m9d.sroom.common.repository.playlist

class PlaylistRepositorySql {

    public static final String GET_LAST_ID = """
        SELECT LAST_INSERT_ID()
    """

    public static final String SAVE = """
        INSERT
        INTO PLAYLIST (playlist_code, channel, thumbnail, description, duration, title, published_at, video_count)
        VALUES (?, ?, ?, ?, ?, ?, ?, ?)
    """

    public static final String GET_BY_ID = """
        SELECT
        playlist_id, playlist_code, channel, thumbnail, accumulated_rating, review_count, is_available, description,
        duration, updated_at, title, published_at, video_count, average_rating
        FROM PLAYLIST
        WHERE playlist_id = ?
    """
    public static final String GET_BY_CODE = """
        SELECT
        playlist_id, playlist_code, channel, thumbnail, accumulated_rating, review_count, is_available, description,
        duration, updated_at, title, published_at, video_count, average_rating
        FROM PLAYLIST
        WHERE playlist_code = ?
    """
    public static final String UPDATE_BY_ID = """
        UPDATE
        PLAYLIST SET
        channel = ?, thumbnail = ?, accumulated_rating = ?, review_count = ?, is_available = ?, description = ?,
        duration = ?, updated_at = ?, title = ?, published_at = ?, video_count = ?, average_rating = ?
        WHERE playlist_id = ?
    """
    public static final String GET_CODE_SET_BY_MEMBER_ID_QUERY = """
        SELECT p.playlist_code 
        FROM LECTURE l JOIN PLAYLIST p ON l.source_id = p.playlist_id 
        WHERE l.member_id = ? AND l.is_playlist = true
    """
    public static final String GET_TOP_RATED_ORDER = """
        SELECT
        playlist_id, playlist_code, channel, thumbnail, accumulated_rating, review_count, is_available, description,
        duration, updated_at, title, published_at, video_count, average_rating
        FROM PLAYLIST 
        ORDER BY average_rating DESC
        LIMIT ?
    """

    public static final String GET_RANDOM_BY_CHANNEL = """
        SELECT
        playlist_id, playlist_code, channel, thumbnail, accumulated_rating, review_count, is_available, description,
        duration, updated_at, title, published_at, video_count, average_rating
        FROM PLAYLIST 
        WHERE channel = ?
        ORDER BY RAND()
        LIMIT ?
    """

    public static final String GET_VIEW_COUNT_ORDER_BY_CHANNEL = """
        SELECT
        p.playlist_id, p.playlist_code, p.channel, p.thumbnail, p.accumulated_rating, p.review_count, p.is_available, 
        p.description, p.duration, p.updated_at, p.title, p.published_at, p.video_count, p.average_rating
        FROM PLAYLIST p
        JOIN PLAYLISTVIDEO pv ON p.playlist_id = pv.playlist_id
        JOIN VIDEO v ON pv.video_id = v.video_id
        WHERE p.channel = ?
        GROUP BY p.playlist_id
        ORDER BY SUM(v.view_count) DESC
        LIMIT ?;
    """

    public static final String GET_LATEST_ORDER_BY_CHANNEL = """
        SELECT
        playlist_id, playlist_code, channel, thumbnail, accumulated_rating, review_count, is_available, description,
        duration, updated_at, title, published_at, video_count, average_rating
        FROM PLAYLIST
        WHERE channel = ?
        ORDER BY published_at DESC
        LIMIT ?
    """

    public static final String UPDATE_RATING = """
        UPDATE PLAYLIST
        SET average_rating = accumulated_rating / review_count
        WHERE review_count > 0;
    """
}
