package com.m9d.sroom.repository.playlistvideo;

import com.m9d.sroom.global.mapper.PlaylistVideo;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class PlaylistVideoJdbcRepositoryImpl implements PlaylistVideoRepository{

    @Override
    public void save(PlaylistVideo playlistVideo) {

    }

    @Override
    public List<PlaylistVideo> getListByPlaylistId(Long playlistId) {
        return null;
    }

    @Override
    public void deleteByPlaylistId(Long playlistId) {

    }
}