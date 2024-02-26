package com.m9d.sroom.common.repository.playlistvideo;

import com.m9d.sroom.common.entity.jdbctemplate.PlaylistVideoEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class PlaylistVideoJdbcRepositoryImpl implements PlaylistVideoRepository {

    private final JdbcTemplate jdbcTemplate;

    public PlaylistVideoJdbcRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public PlaylistVideoEntity save(PlaylistVideoEntity playlistVideo) {
        jdbcTemplate.update(PlaylistVideoRepositorySql.SAVE,
                playlistVideo.getPlaylistId(),
                playlistVideo.getVideoId(),
                playlistVideo.getVideoIndex());
        return getById(jdbcTemplate.queryForObject(PlaylistVideoRepositorySql.GET_LAST_ID, Long.class));
    }

    public PlaylistVideoEntity getById(Long playlistVideoId) {
        return jdbcTemplate.queryForObject(PlaylistVideoRepositorySql.GET_BY_ID, PlaylistVideoEntity.getMapper(), playlistVideoId);
    }

    @Override
    public void deleteByPlaylistId(Long playlistId) {
        jdbcTemplate.update(PlaylistVideoRepositorySql.DELETE_BY_PLAYLIST_ID, playlistId);
    }
}
