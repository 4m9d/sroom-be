package com.m9d.sroom.course.sql

class CourseSqlQuery {

    public static final String GET_CHANNELS_BY_COURSE_ID_QUERY = """
    SELECT l.channel
    FROM LECTURE l
    WHERE l.course_id = ?
    """

    public static final String GET_TOTAL_LECTURE_COUNT_BY_COURSE_ID_QUERY = """
    SELECT COUNT(1) as lecture_count
    FROM COURSEVIDEO c
    WHERE c.course_id = ?
    """

    public static final String GET_COMPLETED_LECTURE_COUNT_BY_COURSE_ID_QUERY = """
    SELECT COUNT(1) as completed_lecture_count
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
    INSERT INTO VIDEO (video_code, duration, channel, thumbnail, description, title, language, license, view_count, published_at)
    VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
    """

    public static final String SAVE_PLAYLIST_QUERY = """
    INSERT INTO PLAYLIST (playlist_code, title, channel, thumbnail, description, published_at, video_count)
    VALUES (?, ?, ?, ?, ?, ?, ?)
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
    SELECT video_id, video_code, channel, thumbnail, language, license, duration, description, title, updated_at, published_at, view_count
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
    SELECT v.duration
    FROM VIDEO v
    JOIN PLAYLISTVIDEO pv 
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
    SET title = ?, channel = ?, description = ?, published_at = ?, video_count = ?, updated_at = current_timestamp()
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
    SELECT member_id, course_title, course_duration, is_scheduled, weeks, start_date, expected_end_date, daily_target_time, thumbnail
    FROM COURSE
    WHERE course_id = ?
    """

    public static final String GET_VIDEO_LIST_BY_COURSE_ID_QUERY = """
    SELECT video_id, video_index, is_complete 
    FROM COURSEVIDEO 
    WHERE course_id = ? 
    ORDER BY video_index
    """

    public static final String GET_LECTURE_INDEX_LIST_QUERY = """
    SELECT lecture_index
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
    WHERE course_id = ? AND video_index = ?
    """

    public static final String UPDATE_SCHEDULE_QUERY = """
    UPDATE COURSE 
    SET weeks = ?, expected_end_date = ? 
    WHERE course_id = ?
    """

    public static final String GET_COURSE_LIST_QUERY = """
    SELECT c.course_title, c.course_id, COUNT(cv.course_id) as video_count
    FROM COURSE c
    LEFT JOIN COURSEVIDEO cv ON c.course_id = cv.course_id
    WHERE c.member_id = ?
    GROUP BY c.course_id;
    """

    public static final String UPDATE_VIDEO_QUERY = """
    UPDATE VIDEO
    SET duration = ?, channel = ?, thumbnail = ?, language = ?, license = ?, description = ?, view_count = ?, title = ?, updated_at = current_timestamp()
    WHERE video_code = ?
    """

    public static final String UPDATE_COURSE_DURATION_QUERY = """
    UPDATE COURSE
    SET course_duration = ?
    WHERE course_id = ?
    """

    public static final String GET_LAST_COURSE_VIDEO = """
    SELECT v.video_id, v.title, v.video_code, v.channel, cv.start_time, cv.course_video_id
    FROM COURSEVIDEO cv
    JOIN video v ON cv.video_id = v.video_id
    WHERE cv.course_id = ?
    ORDER BY cv.last_view_time DESC, cv.video_index ASC
    LIMIT 1
    """

    public static final String GET_VIDEO_BRIEF_QUERY = """
    SELECT v.video_id, v.video_code, v.channel, v.title, cv.video_index, cv.is_complete, cv.start_time, v.duration, cv.course_video_id
    FROM COURSEVIDEO cv
    JOIN video v ON cv.video_id = v.video_id
    WHERE cv.course_id = ? AND cv.section = ?
    ORDER BY cv.video_index ASC
    """

    public static final String GET_LAST_INSERT_ID_QUERY = """
    SELECT LAST_INSERT_ID()
    """

    public static final String FIND_COURSE_VIDEO_ID_QUERY = """
    SELECT course_video_id
    FROM COURSEVIDEO
    WHERE course_id = ?
    AND video_id = ?
    """

    public static final String FIND_COURSE_ID_BY_COURSE_VIDEO_ID = """
       SELECT course_id
       FROM COURSEVIDEO
       WHERE course_video_id = ?
    """

    public static final String FIND_COURSE_VIDEO_BY_ID = """
        SELECT course_video_id, course_id, video_id, section, video_index, start_time, is_complete, summary_id, lecture_index, member_id, last_view_time, max_duration
        FROM COURSEVIDEO
        WHERE course_video_id = ?
    """

    public static final String UPDATE_COURSE_VIDEO = """
        UPDATE COURSEVIDEO
        SET section = ?, video_index = ?, start_time = ?, is_complete = ?, summary_id = ?, lecture_index = ?, last_view_time = ?, max_duration = ?
        WHERE course_video_id = ?
    """

    public static final String FIND_COURSE_DAILY_LOG_QUERY = """
        SELECT course_daily_log_id, member_id, daily_log_date, learning_time, quiz_count, lecture_count
        FROM COURSE_DAILY_LOG
        where course_id = ?
        AND daily_log_date = ?
    """

    public static final String SAVE_COURSE_DAILY_LOG_QUERY = """
        INSERT INTO COURSE_DAILY_LOG (member_id, course_id, daily_log_date, learning_time, quiz_count, lecture_count)
        VALUES (?, ?, ?, ?, ?, ?)
    """

    public static final String UPDATE_COURSE_DAILY_LOG_QUERY = """
        UPDATE COURSE_DAILY_LOG
        SET learning_time = ?, quiz_count = ?, lecture_count = ?
        WHERE course_daily_log_id = ?
    """

    public static final String UPDATE_VIDEO_VIEW_STATUS_QUERY = """
        UPDATE COURSEVIDEO
        SET max_duration = ?, start_time = ?, is_complete = ?, last_view_time = ?
        WHERE course_video_id = ?
    """

    public static final String UPDATE_QUIZ_COUNT_LOG_QUERY = """
        UPDATE COURSE_DAILY_LOG
        SET quiz_count = ?
        WHERE course_id = ?
        AND daily_log_date = ?
    """

    public static final String GET_COURSE_AND_VIDEO_ID_QUERY = """
        SELECT course_id, video_id
        FROM COURSEVIDEO
        WHERE course_video_id = ?
    """

    public static final String GET_QUIZ_COUNT_BY_DAILY_LOG_QUERY = """
        SELECT quiz_count
        FROM COURSE_DAILY_LOG
        WHERE course_id = ?
        AND daily_log_date = ?
    """

    public static final String GET_COURSE_COUNT_BY_MEMBER_ID_QUERY = """
        SELECT COUNT(1)
        FROM COURSE
        WHERE member_id = ?
    """

    public static final String GET_COMPLETED_COURSE_COUNT_BY_MEMBER_Id_QUERY = """
        SELECT COUNT(1)
        FROM COURSE
        WHERE member_id = ? AND progress = 100
    """

    public static final String GET_COURSE_VIDEO_ID_BY_PREV_INDEX_QUERY = """
        SELECT course_video_id
        FROM COURSEVIDEO
        WHERE course_id = ?
        AND video_index > ?
        ORDER BY video_index
        LIMIT 1
    """

    public static final String UPDATE_LAST_VIEW_TIME_BY_ID_QUERY = """
        UPDATE COURSEVIDEO
        SET last_view_time = ?
        WHERE course_video_id = ?
    """
}
