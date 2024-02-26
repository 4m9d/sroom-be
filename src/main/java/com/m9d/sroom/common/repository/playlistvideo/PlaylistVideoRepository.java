package com.m9d.sroom.common.repository.playlistvideo;

import com.m9d.sroom.common.entity.jdbctemplate.PlaylistVideoEntity;

public interface PlaylistVideoRepository {

    PlaylistVideoEntity save(PlaylistVideoEntity playlistVideo);

    void deleteByPlaylistId(Long playlistId);

}
