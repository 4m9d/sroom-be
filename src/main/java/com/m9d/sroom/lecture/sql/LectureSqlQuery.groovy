package com.m9d.sroom.lecture.sql

class LectureSqlQuery {

    public static final String GET_REVIEW_BRIEF_LIST_QUERY = """
    SELECT r.review_id, r.content, r.submitted_rating, m.member_name, r.submitted_date 
    FROM REVIEW r JOIN MEMBER m ON r.member_id = m.member_id 
    WHERE r.source_code = ? 
    ORDER BY r.submitted_date DESC LIMIT ? OFFSET ?
    """

    public static final String GET_VIDEOS_BY_MEMBER_ID_QUERY = """
    SELECT v.video_code 
    FROM COURSEVIDEO cv JOIN VIDEO v ON cv.video_id = v.video_id 
    WHERE cv.member_id = ?
    """

    public static final String GET_PLAYLIST_BY_MEMBER_ID_QUERY = """
    SELECT p.playlist_code 
    FROM LECTURE l JOIN PLAYLIST p ON l.source_id = p.playlist_id 
    WHERE l.member_id = ? AND l.is_playlist = true
    """

    public static final String GET_VIDEOS_SORTED_RATING = """
    SELECT title, description, channel, video_code, review_count, thumbnail, 
        CASE
            WHEN review_count = 0 THEN 0
            ELSE CAST(accumulated_rating AS DOUBLE) / review_count
        END AS rating
    FROM VIDEO 
    ORDER BY rating DESC, RAND()
    LIMIT ?
    """

    public static final String GET_PLAYLISTS_SORTED_RATING = """
    SELECT title, description, channel, playlist_code, review_count, thumbnail, 
        CASE
            WHEN review_count = 0 THEN 0
            ELSE CAST(accumulated_rating AS DOUBLE) / review_count
        END AS rating
    FROM PLAYLIST 
    ORDER BY rating DESC, RAND()
    LIMIT ?
    """

    public static final String GET_MOST_ENROLLED_CHANNELS_BY_MEMBER_ID_QUERY = """
    SELECT channel
    FROM LECTURE
    WHERE member_id = ?
    GROUP BY channel
    ORDER BY COUNT(1) DESC
    """

    public static final String GET_RANDOM_VIDEOS_BY_CHANNEL_QUERY = """
    SELECT title, description, channel, video_code, review_count, thumbnail, 
        CASE
            WHEN review_count = 0 THEN 0
            ELSE CAST(accumulated_rating AS DOUBLE) / review_count
        END AS rating
    FROM VIDEO
    WHERE channel = ?
    ORDER BY RAND()
    LIMIT ?
    """

    public static final String GET_RANDOM_PLAYLISTS_BY_CHANNEL_QUERY = """
    SELECT title, description, channel, playlist_code, review_count, thumbnail, 
        CASE
            WHEN review_count = 0 THEN 0
            ELSE CAST(accumulated_rating AS DOUBLE) / review_count
        END AS rating
    FROM PLAYLIST 
    WHERE channel = ?
    ORDER BY RAND()
    LIMIT ?
    """

    public static final String GET_MOST_VIEWED_VIDEOS_BY_CHANNEL_QUERY = """
    SELECT title, description, channel, video_code, review_count, thumbnail, 
        CASE
            WHEN review_count = 0 THEN 0
            ELSE CAST(accumulated_rating AS DOUBLE) / review_count
        END AS rating
    FROM VIDEO
    WHERE channel = ?
    ORDER BY view_count DESC
    LIMIT ?
    """

    public static final String GET_MOST_VIEWED_PLAYLISTS_BY_CHANNEL_QUERY = """
    SELECT p.title, p.description, p.channel, p.playlist_code, p.review_count, p.thumbnail, 
        CASE
            WHEN p.review_count = 0 THEN 0
            ELSE CAST(p.accumulated_rating AS DOUBLE) / p.review_count
        END AS rating
    FROM playlist p
    JOIN playlistvideo pv ON p.playlist_id = pv.playlist_id
    JOIN video v ON pv.video_id = v.video_id
    WHERE p.channel = ?
    GROUP BY p.playlist_id
    ORDER BY SUM(v.view_count) DESC
    LIMIT ?;
    """

    public static final String GET_LATEST_VIDEOS_BY_CHANNEL_QUERY = """
    SELECT title, description, channel, video_code, review_count, thumbnail, 
        CASE
            WHEN review_count = 0 THEN 0
            ELSE CAST(accumulated_rating AS DOUBLE) / review_count
        END AS rating
    FROM VIDEO
    WHERE channel = ?
    ORDER BY published_at DESC
    LIMIT ?
    """

    public static final String GET_LATEST_PLAYLISTS_BY_CHANNEL_QUERY = """
    SELECT title, description, channel, playlist_code, review_count, thumbnail, 
        CASE
            WHEN review_count = 0 THEN 0
            ELSE CAST(accumulated_rating AS DOUBLE) / review_count
        END AS rating
    FROM PLAYLIST
    WHERE channel = ?
    ORDER BY published_at DESC
    LIMIT ?
    """

    public static final String FIND_VIDEO_BY_ID = """
        SELECT video_code, duration, channel, thumbnail, accumulated_rating, review_count, summary_id, is_available, description, chapter_usage, title, language, license, updated_at, view_count, published_at, membership
        FROM VIDEO
        WHERE video_id = ?
    """

    public static final String FIND_PLAYLIST_BY_ID = """
        SELECT playlist_code, channel, thumbnail, accumulated_rating, review_count, is_available, description, duration, updated_at, title, published_at
        FROM PLAYLIST
        WHERE playlist_id = ?
    """

    public static final String UPDATE_VIDEO = """
        UPDATE VIDEO
        SET duration = ?, channel = ?, thumbnail = ?, accumulated_rating = ?, review_count = ?, description = ?, chapter_usage = ?, title = ?, language = ?, license = ?, updated_at = ?, view_count = ?, membership = ?
        WHERE video_id = ?
    """

    public static final String UPDATE_PLAYLIST = """
        UPDATE PLAYLIST
        SET duration = ?, channel = ?, thumbnail = ?, accumulated_rating = ?, review_count = ?, description = ?, title = ?, updated_at = ?
        WHERE playlist_id = ?
    """

    public static final String UPDATE_COURSE_PROGRESS_QUERY = """
        UPDATE COURSE
        SET PROGRESS = ?
        WHERE course_id = ?
    """

    public static final String GET_VIDEO_COUNT_BY_COURSE_ID = """
        SELECT COUNT(1)
        FROM COURSEVIDEO
        WHERE course_id = ?
    """
}
