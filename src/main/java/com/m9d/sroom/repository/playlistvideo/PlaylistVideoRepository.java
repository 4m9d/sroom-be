package com.m9d.sroom.repository.playlistvideo;

import java.util.List;
import java.util.Map;

public interface PlaylistVideoRepository {

    void save(Long playlistId, Long videoId, int videoIndex);

    List<Map<Long, Integer>> getIndexMapListById(Long playlistId);

    void deleteByPlaylistId(Long playlistId);


}
