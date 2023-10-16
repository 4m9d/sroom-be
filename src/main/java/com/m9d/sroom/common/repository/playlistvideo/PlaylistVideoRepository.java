package com.m9d.sroom.common.repository.playlistvideo;

import com.m9d.sroom.common.dto.PlaylistVideo;

public interface PlaylistVideoRepository {

    PlaylistVideo save(PlaylistVideo playlistVideo);

    void deleteByPlaylistId(Long playlistId);


}
