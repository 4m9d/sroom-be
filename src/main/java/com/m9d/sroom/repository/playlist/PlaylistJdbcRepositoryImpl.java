package com.m9d.sroom.repository.playlist;

import com.m9d.sroom.global.mapper.Playlist;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.Set;

@Repository
public class PlaylistJdbcRepositoryImpl implements PlaylistRepository {

    private final JdbcTemplate jdbcTemplate;

    public PlaylistJdbcRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Playlist save(Playlist playlist) {
        jdbcTemplate.update(PlaylistRepositorySql.SAVE,
                playlist.getPlaylistCode(),
                playlist.getChannel(),
                playlist.getThumbnail(),
                playlist.getDescription(),
                playlist.getDuration(),
                playlist.getTitle(),
                playlist.getPublishedAt(),
                playlist.getVideoCount());
        return getById(jdbcTemplate.queryForObject(PlaylistRepositorySql.GET_LAST_ID, Long.class));
    }

    @Override
    public Playlist getById(Long playlistId) {
        return jdbcTemplate.queryForObject(PlaylistRepositorySql.GET_BY_ID, Playlist.getRowMapper(), playlistId);
    }

    @Override
    public Optional<Playlist> findByCode(String playlistCode) {
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(PlaylistRepositorySql.GET_BY_CODE, Playlist.getRowMapper(), playlistCode));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public Playlist updateById(Long playlistId, Playlist playlist) {
        jdbcTemplate.update(PlaylistRepositorySql.UPDATE_BY_ID,
                playlist.getChannel(),
                playlist.getThumbnail(),
                playlist.getAccumulatedRating(),
                playlist.getReviewCount(),
                playlist.getAvailable(),
                playlist.getDescription(),
                playlist.getDuration(),
                playlist.getUpdatedAt(),
                playlist.getTitle(),
                playlist.getPublishedAt(),
                playlist.getVideoCount(),
                playlistId);
        return getById(playlistId);

    }

    @Override
    public void updateDurationById(Long playlistId, int duration) {

    }

    @Override
    public Set<String> getCodeListByMemberId(Long memberId) {
        return null;
    }
}
