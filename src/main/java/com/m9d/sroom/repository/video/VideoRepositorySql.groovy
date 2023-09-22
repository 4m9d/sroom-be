package com.m9d.sroom.repository.video

class VideoRepositorySql {

    public static final String SAVE = """
        INSERT
        INTO VIDEO(video_code, duration, channel, thumbnail, summary_id, description, title, language, license, 
        view_count, published_at
        VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
    """

    public static final String GET_BY_CODE = """
        SELECT 
        video_id, video_code, duration, channel, thumbnail, accumulated_rating, review_count, summary_id, 
        is_available, description, chapter_usage, title, language, license, updated_at, view_count, published_at, 
        membership, material_status
        FROM VIDEO
        WHERE video_code = ?
    """

    public static final String GET_BY_ID = """
        SELECT 
        video_id, video_code, duration, channel, thumbnail, accumulated_rating, review_count, summary_id, 
        is_available, description, chapter_usage, title, language, license, updated_at, view_count, published_at, 
        membership, material_status
        FROM VIDEO
        WHERE video_id = ?
    """

    public static final String UPDATE_BY_ID = """
        UPDATE VIDEO
        SET video_duration = ?, channel = ?, thumbnail = ?, accumulated_rating = ?, review_count = ?, summary_id = ?,
        is_available = ?, description = ?, chapter_usage = ?, title = ?, language = ?, license = ?, updated_at = ?, 
        view_count = ?, published_at = ?, membership = ?, material_status = ?
        WHERE video_id = ?
    """

    public static final String GET_LIST_BY_PLAYLIST_ID = """
        SELECT
        v.video_id, v.video_code, v.duration, v.channel, v.thumbnail, v.accumulated_rating, v.review_count,
        v.summary_id, v.is_available, v.description, v.chapter_usage, v.title, v.language, v.license, v.updated_at, 
        v.view_count, v.published_at, v.membership, v.material_status
        FROM VIDEO v
        JOIN PLAYLISTVIDEO pv
        ON v.video_id = pv.video_id
        WHERE pv.playlist_id = ?
        ORDER BY pv.video_index
    """
}
