package com.m9d.sroom.repository.playlistvideo;

import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public class PlaylistVideoJdbcRepositoryImpl implements PlaylistVideoRepository{
    @Override
    public void save(Long playlistId, Long videoId, int videoIndex) {

    }

    @Override
    public List<Map<Long, Integer>> getIndexMapListById(Long playlistId) {
        return null;
    }

    @Override
    public void deleteByPlaylistId(Long playlistId) {

    }
}
