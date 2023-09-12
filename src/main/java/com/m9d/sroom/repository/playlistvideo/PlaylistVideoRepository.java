package com.m9d.sroom.repository.playlistvideo;

import com.m9d.sroom.global.model.PlaylistVideo;

import java.util.List;
import java.util.Map;

public interface PlaylistVideoRepository {

    void save(PlaylistVideo playlistVideo);

    List<PlaylistVideo> getListByPlaylistId(Long playlistId);

    void deleteByPlaylistId(Long playlistId);


}
