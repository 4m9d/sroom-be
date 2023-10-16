package com.m9d.sroom.common.repository.playlist;

import com.m9d.sroom.common.dto.Playlist;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

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
    public List<Playlist> getTopRatedOrder(int limit) {
        return jdbcTemplate.query(PlaylistRepositorySql.GET_TOP_RATED_ORDER, Playlist.getRowMapper(), limit);
    }

    @Override
    public HashSet<String> getCodeSetByMemberId(Long memberId) {
        return new HashSet<>(jdbcTemplate.query(PlaylistRepositorySql.GET_CODE_SET_BY_MEMBER_ID_QUERY,
                (rs, rowNum) -> rs.getString("playlist_code"), memberId));
    }

    @Override
    public List<Playlist> getRandomByChannel(String channel, int limit) {
        return jdbcTemplate.query(PlaylistRepositorySql.GET_RANDOM_BY_CHANNEL, Playlist.getRowMapper(), channel, limit);
    }

    @Override
    public List<Playlist> getViewCountOrderByChannel(String channel, int limit) {
        return jdbcTemplate.query(PlaylistRepositorySql.GET_VIEW_COUNT_ORDER_BY_CHANNEL, Playlist.getRowMapper(), channel, limit);
    }

    @Override
    public List<Playlist> getLatestOrderByChannel(String channel, int limit) {
        return jdbcTemplate.query(PlaylistRepositorySql.GET_LATEST_ORDER_BY_CHANNEL, Playlist.getRowMapper(), channel, limit);
    }
}
