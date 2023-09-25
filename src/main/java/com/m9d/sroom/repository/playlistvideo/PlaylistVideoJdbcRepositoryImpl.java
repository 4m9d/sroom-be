package com.m9d.sroom.repository.playlistvideo;

import com.m9d.sroom.global.mapper.PlaylistVideo;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public class PlaylistVideoJdbcRepositoryImpl implements PlaylistVideoRepository {

    private final JdbcTemplate jdbcTemplate;

    public PlaylistVideoJdbcRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    @Transactional
    public PlaylistVideo save(PlaylistVideo playlistVideo) {
        jdbcTemplate.update(PlaylistVideoRepositorySql.SAVE,
                playlistVideo.getPlaylistId(),
                playlistVideo.getVideoId(),
                playlistVideo.getVideoIndex());
        return getById(jdbcTemplate.queryForObject(PlaylistVideoRepositorySql.GET_LAST_ID, Long.class));
    }

    private PlaylistVideo getById(Long playlistVideoId) {
        return jdbcTemplate.queryForObject(PlaylistVideoRepositorySql.GET_BY_ID, PlaylistVideo.getMapper(), playlistVideoId);
    }

    @Override
    public List<PlaylistVideo> getListByPlaylistId(Long playlistId) {
        return null;
    }

    @Override
    public void deleteByPlaylistId(Long playlistId) {
        jdbcTemplate.update(PlaylistVideoRepositorySql.DELETE_BY_PLAYLIST_ID, playlistId);
    }
}
