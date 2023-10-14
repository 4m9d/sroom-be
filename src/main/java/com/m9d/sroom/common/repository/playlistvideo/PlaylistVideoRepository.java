package com.m9d.sroom.common.repository.playlistvideo;

import com.m9d.sroom.common.dto.PlaylistVideoDto;

public interface PlaylistVideoRepository {

    PlaylistVideoDto save(PlaylistVideoDto playlistVideoDto);

    void deleteByPlaylistId(Long playlistId);


}
