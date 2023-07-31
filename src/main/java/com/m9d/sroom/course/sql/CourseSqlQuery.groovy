package com.m9d.sroom.course.sql

class CourseSqlQuery {
  
    public static final String GET_CHANNELS_BY_COURSE_ID_QUERY = """
    SELECT l.channel
    FROM LECTURE l
    WHERE l.course_id = ?
    """;

    public static final String GET_TOTAL_LECTURE_COUNT_BY_COURSE_ID_QUERY = """
    SELECT COUNT(*) as lecture_count
    FROM COURSEVIDEO c
    WHERE c.course_id = ?
    """

    public static final String GET_COMPLETED_LECTURE_COUNT_BY_COURSE_ID_QUERY = """
    SELECT COUNT(*) as completed_lecture_count
    FROM COURSEVIDEO cv
    WHERE cv.course_id = ? AND cv.is_complete = true
    """

    public static final String GET_COURSES_BY_MEMBER_ID_QUERY = """
    SELECT c.course_id, c.thumbnail, c.course_duration, c.last_view_time, c.course_title, c.progress
    FROM COURSE c
    WHERE c.member_id = ?
    ORDER BY c.last_view_time DESC
    """
  
    public static final String SAVE_COURSE_QUERY = """
    INSERT INTO COURSE (member_id, course_title, course_duration, thumbnail)
    VALUES (?, ?, ?, ?)
    """

    public static final String SAVE_COURSE_WITH_SCHEDULE_QUERY = """
    INSERT INTO COURSE (member_id, course_title, course_duration, thumbnail, weeks, daily_target_time, is_scheduled, expected_end_date)
    VALUES (?, ?, ?, ?, ?, ?, ?, ?)
    """

    public static final String SAVE_VIDEO_QUERY = """
    INSERT INTO VIDEO (video_code, duration, channel, thumbnail, description, title, language, license)
    VALUES (?, ?, ?, ?, ?, ?, ?, ?)
    """

    public static final String SAVE_PLAYLIST_QUERY = """
    INSERT INTO PLAYLIST (playlist_code, title, channel, thumbnail, description, published_at)
    VALUES (?, ?, ?, ?, ?, ?)
    """

    public static final String SAVE_PLAYLIST_VIDEO_QUERY = """
    INSERT INTO PLAYLISTVIDEO (playlist_id, video_id, video_index)
    VALUES (?, ?, ?)
    """

    public static final String SAVE_LECTURE_QUERY = """
    INSERT INTO LECTURE (member_id, course_id, source_id, channel, is_playlist, lecture_index)
    VALUES (?, ?, ?, ?, ?, ?)
    """

    public static final String SAVE_COURSE_VIDEO_QUERY = """
    INSERT INTO COURSEVIDEO (member_id, course_id, video_id, section, video_index, lecture_index)
    VALUES (?, ?, ?, ?, ?, ?)
    """

    public static final String GET_COURSE_ID_BY_LECTURE_ID_QUERY = """
    SELECT course_id FROM LECTURE
    WHERE lecture_id = ?
    """

    public static final String FIND_PLAYLIST_QUERY = """
    SELECT playlist_id, title, channel, thumbnail, description, duration, updated_at
    FROM PLAYLIST
    WHERE playlist_code = ?
    """

    public static final String FIND_VIDEO_QUERY = """
    SELECT video_id, video_code, channel, thumbnail, language, license, duration, description, title, updated_at 
    FROM VIDEO 
    WHERE video_code = ?
    """

    public static final String GET_VIDEO_ID_AND_INDEX_QUERY = """
    SELECT video_id, video_index 
    FROM PLAYLISTVIDEO 
    WHERE playlist_id = ? 
    ORDER BY video_index
    """

    public static final String GET_DURATION_BY_PLAYLIST_ID_QUERY = """
    SELECT SUM(v.duration)
    FROM VIDEO v
    INNER JOIN PLAYLISTVIDEO pv 
    ON v.video_id = pv.video_id
    WHERE pv.playlist_id = ?
    """

    public static final String GET_MEMBER_ID_BY_COURSE_ID_QUERY = """
    SELECT member_id 
    FROM COURSE 
    WHERE course_id = ?
    """

    public static final String UPDATE_PLAYLIST_AND_GET_ID_QUERY = """
    UPDATE PLAYLIST 
    SET title = ?, channel = ?, description = ?, published_at = ? 
    WHERE playlist_code = ?
    """

    public static final String GET_PLAYLIST_ID_BY_PLAYLIST_CODE = """
    SELECT playlist_id 
    FROM PLAYLIST 
    WHERE playlist_code = ?
    """

    public static final String DELETE_PLAYLIST_VIDEO_QUERY = """
    DELETE FROM PLAYLISTVIDEO 
    WHERE playlist_id = ?
    """

    public static final String UPDATE_PLAYLIST_DURATION_QUERY = """
    UPDATE PLAYLIST 
    SET duration = ? 
    WHERE playlist_id = ?
    """

    public static final String GET_COURSE_QUERY = """
    SELECT member_id, course_title, course_duration, is_scheduled, weeks, start_date, expected_end_date, daily_target_time
    FROM COURSE
    WHERE course_id = ?
    """

    public static final String GET_VIDEO_LIST_BY_COURSE_ID_QUERY = """
    SELECT video_id, video_index, is_complete 
    FROM COURSEVIDEO 
    WHERE course_id = ? 
    ORDER BY video_index
    """

    public static final String GET_LAST_LECTURE_INDEX_QUERY = """
    SELECT MAX(lecture_index) 
    FROM LECTURE 
    WHERE course_id = ?
    """

    public static final String GET_VIDEOS_BY_COURSE_ID_QUERY = """
    SELECT v.video_id, cv.video_index, v.duration
    FROM COURSEVIDEO cv
    INNER JOIN VIDEO v ON cv.video_id = v.video_id
    WHERE cv.course_id = ?
    ORDER BY cv.video_index ASC
    """

    public static final String UPDATE_VIDEO_SECTION_QUERY = """
    UPDATE COURSEVIDEO SET section = ? 
    WHERE course_id = ? AND video_id = ?
    """

    public static final String UPDATE_SCHEDULE_QUERY = """
    UPDATE COURSE 
    SET weeks = ?, expected_end_date = ? 
    WHERE course_id = ?
    """

    public static final String GET_LAST_INSERT_ID_QUERY = """
    SELECT LAST_INSERT_ID()
    """
}
