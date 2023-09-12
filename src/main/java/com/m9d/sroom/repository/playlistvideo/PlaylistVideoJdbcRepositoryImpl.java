package com.m9d.sroom.repository.playlistvideo;

import com.m9d.sroom.global.model.PlaylistVideo;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public class PlaylistVideoJdbcRepositoryImpl implements PlaylistVideoRepository{
    @Override
    public void save(Long playlistId, Long videoId, int videoIndex) {

    }

    @Override
    public List<PlaylistVideo> getListByPlaylistId(Long playlistId) {
        return null;
    }

    @Override
    public void deleteByPlaylistId(Long playlistId) {

    }
}
