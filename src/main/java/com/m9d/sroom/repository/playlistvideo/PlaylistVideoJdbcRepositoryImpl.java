package com.m9d.sroom.repository.playlistvideo;

import com.m9d.sroom.global.mapper.PlaylistVideoDto;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class PlaylistVideoJdbcRepositoryImpl implements PlaylistVideoRepository {

    private final JdbcTemplate jdbcTemplate;

    public PlaylistVideoJdbcRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public PlaylistVideoDto save(PlaylistVideoDto playlistVideoDto) {
        jdbcTemplate.update(PlaylistVideoRepositorySql.SAVE,
                playlistVideoDto.getPlaylistId(),
                playlistVideoDto.getVideoId(),
                playlistVideoDto.getVideoIndex());
        return getById(jdbcTemplate.queryForObject(PlaylistVideoRepositorySql.GET_LAST_ID, Long.class));
    }

    private PlaylistVideoDto getById(Long playlistVideoId) {
        return jdbcTemplate.queryForObject(PlaylistVideoRepositorySql.GET_BY_ID, PlaylistVideoDto.getMapper(), playlistVideoId);
    }

    @Override
    public void deleteByPlaylistId(Long playlistId) {
        jdbcTemplate.update(PlaylistVideoRepositorySql.DELETE_BY_PLAYLIST_ID, playlistId);
    }
}
