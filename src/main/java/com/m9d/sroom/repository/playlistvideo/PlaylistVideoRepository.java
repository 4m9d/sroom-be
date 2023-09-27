package com.m9d.sroom.repository.playlistvideo;

import com.m9d.sroom.global.mapper.PlaylistVideo;

import java.util.List;

public interface PlaylistVideoRepository {

    PlaylistVideo save(PlaylistVideo playlistVideo);

    List<PlaylistVideo> getListByPlaylistId(Long playlistId);

    void deleteByPlaylistId(Long playlistId);


}
