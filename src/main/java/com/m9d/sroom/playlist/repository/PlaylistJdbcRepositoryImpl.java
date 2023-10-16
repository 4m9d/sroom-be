package com.m9d.sroom.playlist.repository;

import com.m9d.sroom.playlist.PlaylistDto;
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
    public PlaylistDto save(PlaylistDto playlistDto) {
        jdbcTemplate.update(PlaylistRepositorySql.SAVE,
                playlistDto.getPlaylistCode(),
                playlistDto.getChannel(),
                playlistDto.getThumbnail(),
                playlistDto.getDescription(),
                playlistDto.getDuration(),
                playlistDto.getTitle(),
                playlistDto.getPublishedAt(),
                playlistDto.getVideoCount());
        return getById(jdbcTemplate.queryForObject(PlaylistRepositorySql.GET_LAST_ID, Long.class));
    }

    @Override
    public PlaylistDto getById(Long playlistId) {
        return jdbcTemplate.queryForObject(PlaylistRepositorySql.GET_BY_ID, PlaylistDto.getRowMapper(), playlistId);
    }

    @Override
    public PlaylistDto getByCode(String playlistCode) {
        return jdbcTemplate.queryForObject(PlaylistRepositorySql.GET_BY_CODE, PlaylistDto.getRowMapper(), playlistCode);
    }

    @Override
    public Optional<PlaylistDto> findByCode(String playlistCode) {
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(PlaylistRepositorySql.GET_BY_CODE, PlaylistDto.getRowMapper(), playlistCode));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public PlaylistDto updateById(Long playlistId, PlaylistDto playlistDto) {
        jdbcTemplate.update(PlaylistRepositorySql.UPDATE_BY_ID,
                playlistDto.getChannel(),
                playlistDto.getThumbnail(),
                playlistDto.getAccumulatedRating(),
                playlistDto.getReviewCount(),
                playlistDto.getAvailable(),
                playlistDto.getDescription(),
                playlistDto.getDuration(),
                playlistDto.getUpdatedAt(),
                playlistDto.getTitle(),
                playlistDto.getPublishedAt(),
                playlistDto.getVideoCount(),
                playlistId);
        return getById(playlistId);

    }

    @Override
    public List<PlaylistDto> getTopRatedOrder(int limit) {
        return jdbcTemplate.query(PlaylistRepositorySql.GET_TOP_RATED_ORDER, PlaylistDto.getRowMapper(), limit);
    }

    @Override
    public HashSet<String> getCodeSetByMemberId(Long memberId) {
        return new HashSet<>(jdbcTemplate.query(PlaylistRepositorySql.GET_CODE_SET_BY_MEMBER_ID_QUERY,
                (rs, rowNum) -> rs.getString("playlist_code"), memberId));
    }

    @Override
    public List<PlaylistDto> getRandomByChannel(String channel, int limit) {
        return jdbcTemplate.query(PlaylistRepositorySql.GET_RANDOM_BY_CHANNEL, PlaylistDto.getRowMapper(), channel, limit);
    }

    @Override
    public List<PlaylistDto> getViewCountOrderByChannel(String channel, int limit) {
        return jdbcTemplate.query(PlaylistRepositorySql.GET_VIEW_COUNT_ORDER_BY_CHANNEL, PlaylistDto.getRowMapper(), channel, limit);
    }

    @Override
    public List<PlaylistDto> getLatestOrderByChannel(String channel, int limit) {
        return jdbcTemplate.query(PlaylistRepositorySql.GET_LATEST_ORDER_BY_CHANNEL, PlaylistDto.getRowMapper(), channel, limit);
    }
}
