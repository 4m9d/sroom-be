package com.m9d.sroom.common.repository.video;

import com.m9d.sroom.common.entity.jdbctemplate.VideoEntity;
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
    public VideoEntity save(VideoEntity video) {
        jdbcTemplate.update(VideoRepositorySql.SAVE,
                video.getVideoCode(),
                video.getDuration(),
                video.getChannel(),
                video.getThumbnail(),
                video.getSummaryId(),
                video.getDescription(),
                video.getTitle(),
                video.getLanguage(),
                video.getLicense(),
                video.getViewCount(),
                video.getPublishedAt());
        return getByCode(video.getVideoCode());
    }

    @Override
    public VideoEntity getByCode(String videoCode) {
        return jdbcTemplate.queryForObject(VideoRepositorySql.GET_BY_CODE, VideoEntity.getRowMapper(), videoCode);
    }

    @Override
    public VideoEntity getById(Long videoId) {
        return jdbcTemplate.queryForObject(VideoRepositorySql.GET_BY_ID, VideoEntity.getRowMapper(), videoId);
    }

    @Override
    public Optional<VideoEntity> findByCode(String videoCode) {
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(VideoRepositorySql.GET_BY_CODE, VideoEntity.getRowMapper(), videoCode));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<VideoEntity> getTopRatedOrder(int limit) {
        return jdbcTemplate.query(VideoRepositorySql.GET_TOP_RATED_ORDER, VideoEntity.getRowMapper(), limit);
    }

    @Override
    public Optional<VideoEntity> findById(Long videoId) {
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(VideoRepositorySql.GET_BY_ID, VideoEntity.getRowMapper(), videoId));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public VideoEntity updateById(Long videoId, VideoEntity video) {
        jdbcTemplate.update(VideoRepositorySql.UPDATE_BY_ID,
                video.getDuration(),
                video.getChannel(),
                video.getThumbnail(),
                video.getAccumulatedRating(),
                video.getReviewCount(),
                video.getSummaryId(),
                video.isAvailable(),
                video.getDescription(),
                video.isChapterUse(),
                video.getTitle(),
                video.getLanguage(),
                video.getLicense(),
                video.getUpdatedAt(),
                video.getViewCount(),
                video.getPublishedAt(),
                video.isMembership(),
                video.getMaterialStatus(),
                video.getAverageRating(),
                video.getVideoId());
        return getByCode(video.getVideoCode());
    }

    @Override
    public List<VideoEntity> getListByPlaylistId(Long playlistId) {
        return jdbcTemplate.query(VideoRepositorySql.GET_LIST_BY_PLAYLIST_ID, VideoEntity.getRowMapper(), playlistId);
    }

    @Override
    public Set<String> getCodeSetByMemberId(Long memberId) {
        return new HashSet<>(jdbcTemplate.query(VideoRepositorySql.GET_CODE_SET_BY_MEMBER_ID, (rs, rowNum) ->
                rs.getString("video_code"), memberId));
    }

    @Override
    public List<VideoEntity> getRandomByChannel(String channel, int limit) {
        return jdbcTemplate.query(VideoRepositorySql.GET_RANDOM_BY_CHANNEL, VideoEntity.getRowMapper(), channel, limit);
    }

    @Override
    public List<VideoEntity> getViewCountOrderByChannel(String channel, int limit) {
        return jdbcTemplate.query(VideoRepositorySql.GET_VIEW_COUNT_ORDER_BY_CHANNEL, VideoEntity.getRowMapper(), channel, limit);
    }

    @Override
    public List<VideoEntity> getLatestOrderByChannel(String channel, int limit) {
        return jdbcTemplate.query(VideoRepositorySql.GET_LATEST_ORDER_BY_CHANNEL, VideoEntity.getRowMapper(), channel, limit);
    }

    @Override
    public Integer updateRating() {
        return jdbcTemplate.update(VideoRepositorySql.UPDATE_RATING);
    }
}
