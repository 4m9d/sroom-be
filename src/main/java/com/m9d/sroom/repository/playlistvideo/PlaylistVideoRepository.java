package com.m9d.sroom.repository.playlistvideo;

import com.m9d.sroom.global.mapper.PlaylistVideoDto;

public interface PlaylistVideoRepository {

    PlaylistVideoDto save(PlaylistVideoDto playlistVideoDto);

    void deleteByPlaylistId(Long playlistId);


}
