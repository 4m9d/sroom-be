package com.m9d.sroom.common.repository.playlistvideo

class PlaylistVideoRepositorySql {

    public static final String GET_LAST_ID = """
        SELECT LAST_INSERT_ID()
    """

    public static final String SAVE = """
        INSERT
        INTO playlistvideo (playlist_id, video_id, video_index)
        VALUES (?, ?, ?)
    """

    public static final String GET_BY_ID = """
        SELECT
        playlist_video_id, playlist_id, video_id, video_index
        FROM playlistvideo
        WHERE playlist_video_id = ?
    """

    public static final String DELETE_BY_PLAYLIST_ID = """
        DELETE
        FROM playlistvideo
        WHERE playlist_id = ?
    """
    public static final String GET_LIST_BY_PLAYLIST_ID = """
        SELECT
        playlist_video_id, playlist_id, video_id, video_index
        FROM 
    """
}
