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
}
