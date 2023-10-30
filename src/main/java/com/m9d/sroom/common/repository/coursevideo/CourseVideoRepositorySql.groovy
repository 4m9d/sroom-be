package com.m9d.sroom.common.repository.coursevideo

class CourseVideoRepositorySql {

    public static final String GET_LAST_ID = """
        SELECT LAST_INSERT_ID()
    """

    public static final String SAVE = """
        INSERT
        INTO COURSEVIDEO (course_id, video_id, section, video_index, summary_id, lecture_index, member_id, 
        last_view_time, max_duration, lecture_id)
        VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
    """

    public static final String GET_BY_ID = """
        SELECT
        course_video_id, course_id, video_id, section, video_index, start_time, is_complete, summary_id, lecture_index,
        member_id, last_view_time, max_duration, lecture_id
        FROM COURSEVIDEO
        WHERE course_video_id = ?
    """

    public static final String GET_LIST_BY_COURSE_ID = """
        SELECT
        course_video_id, course_id, video_id, section, video_index, start_time, is_complete, summary_id, lecture_index, 
        member_id, last_view_time, max_duration, lecture_id
        FROM COURSEVIDEO
        WHERE course_id = ?
        ORDER BY video_index;
    """

    public static final String GET_LIST_BY_LECTURE_ID = """
        SELECT
        course_video_id, course_id, video_id, section, video_index, start_time, is_complete, summary_id, lecture_index, 
        member_id, last_view_time, max_duration, lecture_id
        FROM COURSEVIDEO
        WHERE lecture_id = ?
    """

    public static final String GET_BY_COURSE_ID_AND_INDEX = """
        SELECT
        course_video_id, course_id, video_id, section, video_index, start_time, is_complete, summary_id, lecture_index,
        member_id, last_view_time, max_duration, lecture_id
        FROM COURSEVIDEO
        WHERE course_id = ? AND video_index = ?
    """

    public static final String COOUNT_BY_COURSE_ID = """
        SELECT COUNT(1) as count
        FROM COURSEVIDEO
        WHERE course_id = ?
    """

    public static final String COMPLETED_COUNT_BY_COURSE_ID = """
        SELECT COUNT(1) as completed_count
        FROM COURSEVIDEO
        WHERE course_id = ? AND is_complete = true
    """

    public static final String GET_LAST_INFO_BY_COURSE_ID = """
        SELECT v.video_id, v.title, v.video_code, v.channel, cv.start_time, cv.course_video_id
        FROM COURSEVIDEO cv
        JOIN VIDEO v ON cv.video_id = v.video_id
        WHERE cv.course_id = ?
        ORDER BY cv.last_view_time DESC, cv.video_index ASC
        LIMIT 1
    """

    public static final String GET_WATCH_INFO_LIST_BY_COURSE_ID_AND_SECTION = """
        SELECT 
        v.video_id, v.video_code, v.channel, v.title, cv.video_index, cv.is_complete, 
        cv.start_time, v.duration, cv.course_video_id, cv.max_duration
        FROM COURSEVIDEO cv
        JOIN VIDEO v ON cv.video_id = v.video_id
        WHERE cv.course_id = ? AND cv.section = ?
        ORDER BY cv.video_index ASC
    """

    public static final String GET_INFO_FOR_SCHEDULE_BY_COURSE_ID = """
        SELECT 
        cv.course_video_id, cv.course_id, cv.lecture_id, cv.video_id, cv.section, 
        cv.video_index, cv.start_time, cv.is_complete, cv.summary_id, cv.lecture_index, 
        cv.member_id, cv.last_view_time, cv.max_duration, v.duration 
        FROM COURSEVIDEO cv 
        JOIN VIDEO v ON cv.video_id = v.video_id 
        WHERE cv.course_id = ? 
        ORDER BY cv.video_index ASC
    """

    public static final String DELETE_BY_COURSE_ID = """
        DELETE FROM COURSEVIDEO
        WHERE course_id = ?
    """

    public static final String UPDATE_BY_ID = """
        UPDATE COURSEVIDEO
        SET course_id = ?, video_id = ?, section = ?, video_index = ?, start_time = ?, is_complete = ?, summary_id = ?,
        lecture_index = ?, member_id = ?, last_view_time = ?, max_duration =?, lecture_id = ?
        WHERE course_video_id = ?
    """
    public static final String UPDATE_SUMMARY_ID = """
        UPDATE COURSEVIDEO
        SET summary_id = ?
        WHERE video_id = ?
        AND (summary_id = 0 OR summary_id IS NULL)
    """
    public static final String GET_BY_COURSE_ID_AND_PREV_INDEX = """
        SELECT course_video_id, course_id, video_id, section, video_index, start_time, is_complete, summary_id, 
        lecture_index, member_id, last_view_time, max_duration, lecture_id
        FROM COURSEVIDEO
        WHERE course_id = ?
        AND video_index > ?
        ORDER BY video_index
        LIMIT 1
    """
}
