package com.m9d.sroom.repository.playlist

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
        duration, updated_at, title, published_at, video_count
        FROM PLAYLIST
        WHERE playlist_id = ?
    """
    public static final String GET_BY_CODE = """
        SELECT
        playlist_id, playlist_code, channel, thumbnail, accumulated_rating, review_count, is_available, description,
        duration, updated_at, title, published_at, video_count
        FROM PLAYLIST
        WHERE playlist_code = ?
    """
    public static final String UPDATE_BY_ID = """
        UPDATE
        PLAYLIST SET
        channel = ?, thumbnail = ?, accumulated_rating = ?, review_count = ?, is_available = ?, description = ?,
        duration = ?, updated_at = ?, title = ?, published_at = ?, video_count = ?
        WHERE playlist_id = ?
    """
}
