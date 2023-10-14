package com.m9d.sroom.video.repository;

import com.m9d.sroom.video.VideoDto;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public class VideoJdbcRepositoryImpl implements VideoRepository {

    private final JdbcTemplate jdbcTemplate;

    public VideoJdbcRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public VideoDto save(VideoDto videoDto) {
        jdbcTemplate.update(VideoRepositorySql.SAVE,
                videoDto.getVideoCode(),
                videoDto.getDuration(),
                videoDto.getChannel(),
                videoDto.getThumbnail(),
                videoDto.getSummaryId(),
                videoDto.getDescription(),
                videoDto.getTitle(),
                videoDto.getLanguage(),
                videoDto.getLicense(),
                videoDto.getViewCount(),
                videoDto.getPublishedAt());
        return getByCode(videoDto.getVideoCode());
    }

    @Override
    public VideoDto getByCode(String videoCode) {
        return jdbcTemplate.queryForObject(VideoRepositorySql.GET_BY_CODE, VideoDto.getRowMapper(), videoCode);
    }

    @Override
    public VideoDto getById(Long videoId) {
        return jdbcTemplate.queryForObject(VideoRepositorySql.GET_BY_ID, VideoDto.getRowMapper(), videoId);
    }

    @Override
    public Optional<VideoDto> findByCode(String videoCode) {
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(VideoRepositorySql.GET_BY_CODE, VideoDto.getRowMapper(), videoCode));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<VideoDto> getTopRatedOrder(int limit) {
        return jdbcTemplate.query(VideoRepositorySql.GET_TOP_RATED_ORDER, VideoDto.getRowMapper(), limit);
    }

    @Override
    public Optional<VideoDto> findById(Long videoId) {
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(VideoRepositorySql.GET_BY_ID, VideoDto.getRowMapper(), videoId));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public VideoDto updateById(Long videoId, VideoDto videoDto) {
        jdbcTemplate.update(VideoRepositorySql.UPDATE_BY_ID,
                videoDto.getDuration(),
                videoDto.getChannel(),
                videoDto.getThumbnail(),
                videoDto.getAccumulatedRating(),
                videoDto.getReviewCount(),
                videoDto.getSummaryId(),
                videoDto.isAvailable(),
                videoDto.getDescription(),
                videoDto.isChapterUse(),
                videoDto.getTitle(),
                videoDto.getLanguage(),
                videoDto.getLicense(),
                videoDto.getUpdatedAt(),
                videoDto.getViewCount(),
                videoDto.getPublishedAt(),
                videoDto.isMembership(),
                videoDto.getMaterialStatus(),
                videoDto.getVideoId());
        return getByCode(videoDto.getVideoCode());
    }

    @Override
    public List<VideoDto> getListByPlaylistId(Long playlistId) {
        return jdbcTemplate.query(VideoRepositorySql.GET_LIST_BY_PLAYLIST_ID, VideoDto.getRowMapper(), playlistId);
    }

    @Override
    public Set<String> getCodeSetByMemberId(Long memberId) {
        return new HashSet<>(jdbcTemplate.query(VideoRepositorySql.GET_CODE_SET_BY_MEMBER_ID, (rs, rowNum) ->
                rs.getString("video_code"), memberId));
    }

    @Override
    public List<VideoDto> getRandomByChannel(String channel, int limit) {
        return jdbcTemplate.query(VideoRepositorySql.GET_RANDOM_BY_CHANNEL, VideoDto.getRowMapper(), channel, limit);
    }

    @Override
    public List<VideoDto> getViewCountOrderByChannel(String channel, int limit) {
        return jdbcTemplate.query(VideoRepositorySql.GET_VIEW_COUNT_ORDER_BY_CHANNEL, VideoDto.getRowMapper(), channel, limit);
    }

    @Override
    public List<VideoDto> getLatestOrderByChannel(String channel, int limit) {
        return jdbcTemplate.query(VideoRepositorySql.GET_LATEST_ORDER_BY_CHANNEL, VideoDto.getRowMapper(), channel, limit);
    }
}
