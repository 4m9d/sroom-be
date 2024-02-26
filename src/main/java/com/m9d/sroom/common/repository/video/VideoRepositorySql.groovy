package com.m9d.sroom.common.repository.video

class VideoRepositorySql {

    public static final String SAVE = """
        INSERT
        INTO video (video_code, duration, channel, thumbnail, summary_id, description, title, language, license, 
        view_count, published_at)
        VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
    """

    public static final String GET_BY_CODE = """
        SELECT 
        video_id, video_code, duration, channel, thumbnail, accumulated_rating, review_count, summary_id, 
        is_available, description, chapter_usage, title, language, license, updated_at, view_count, published_at, 
        membership, material_status, average_rating
        FROM video
        WHERE video_code = ?
    """

    public static final String GET_BY_ID = """
        SELECT 
        video_id, video_code, duration, channel, thumbnail, accumulated_rating, review_count, summary_id, 
        is_available, description, chapter_usage, title, language, license, updated_at, view_count, published_at, 
        membership, material_status, average_rating
        FROM video
        WHERE video_id = ?
    """

    public static final String GET_TOP_RATED_ORDER = """
        SELECT 
        video_id, video_code, duration, channel, thumbnail, accumulated_rating, review_count, summary_id, 
        is_available, description, chapter_usage, title, language, license, updated_at, view_count, published_at, 
        membership, material_status, average_rating
        FROM video 
        ORDER BY average_rating DESC
        LIMIT ? 
    """

    public static final String UPDATE_BY_ID = """
        UPDATE video
        SET duration = ?, channel = ?, thumbnail = ?, accumulated_rating = ?, review_count = ?, summary_id = ?,
        is_available = ?, description = ?, chapter_usage = ?, title = ?, language = ?, license = ?, updated_at = ?, 
        view_count = ?, published_at = ?, membership = ?, material_status = ?, average_rating = ?
        WHERE video_id = ?
    """

    public static final String GET_LIST_BY_PLAYLIST_ID = """
        SELECT
        v.video_id, v.video_code, v.duration, v.channel, v.thumbnail, v.accumulated_rating, v.review_count,
        v.summary_id, v.is_available, v.description, v.chapter_usage, v.title, v.language, v.license, v.updated_at, 
        v.view_count, v.published_at, v.membership, v.material_status, pv.video_index, v.average_rating
        FROM video v
        JOIN playlistvideo pv
        ON v.video_id = pv.video_id
        WHERE pv.playlist_id = ?
        ORDER BY pv.video_index
    """

    public static final String GET_CODE_SET_BY_MEMBER_ID = """
        SELECT v.video_code 
        FROM coursevideo cv JOIN video v ON cv.video_id = v.video_id 
        WHERE cv.member_id = ?
    """

    public static final String GET_RANDOM_BY_CHANNEL = """
        SELECT 
        video_id, video_code, duration, channel, thumbnail, accumulated_rating, review_count, summary_id, 
        is_available, description, chapter_usage, title, language, license, updated_at, view_count, published_at,
        membership, material_status, average_rating
        FROM video
        WHERE channel = ?
        ORDER BY RAND()
        LIMIT ?
    """

    public static final String GET_VIEW_COUNT_ORDER_BY_CHANNEL = """
        SELECT 
        video_id, video_code, duration, channel, thumbnail, accumulated_rating, review_count, summary_id, 
        is_available, description, chapter_usage, title, language, license, updated_at, view_count, published_at, 
        membership, material_status, average_rating
        FROM video
        WHERE channel = ?
        ORDER BY view_count DESC
        LIMIT ?
    """

    public static final String GET_LATEST_ORDER_BY_CHANNEL = """
        SELECT 
        video_id, video_code, duration, channel, thumbnail, accumulated_rating, review_count, summary_id, 
        is_available, description, chapter_usage, title, language, license, updated_at, view_count, published_at, 
        membership, material_status, average_rating
        FROM video
        WHERE channel = ?
        ORDER BY published_at DESC
        LIMIT ?
    """

    public static final String UPDATE_RATING = """
        UPDATE video
        SET average_rating = accumulated_rating / review_count
        WHERE review_count > 0;
    """
}
