package com.m9d.sroom.repository.video;

import com.m9d.sroom.global.mapper.Video;
import com.m9d.sroom.lecture.sql.LectureSqlQuery;
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
    public Video save(Video video) {
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
    public Video getByCode(String videoCode) {
        return jdbcTemplate.queryForObject(VideoRepositorySql.GET_BY_CODE, Video.getRowMapper(), videoCode);
    }

    @Override
    public Video getById(Long videoId) {
        return jdbcTemplate.queryForObject(VideoRepositorySql.GET_BY_ID, Video.getRowMapper(), videoId);
    }

    @Override
    public Optional<Video> findByCode(String videoCode) {
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(VideoRepositorySql.GET_BY_CODE, Video.getRowMapper(), videoCode));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<Video> findById(Long videoId) {
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(VideoRepositorySql.GET_BY_ID, Video.getRowMapper(), videoId));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public Video updateById(Long videoId, Video video) {
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
                video.getVideoId());
        return getByCode(video.getVideoCode());
    }

    @Override
    public List<Video> getListByPlaylistId(Long playlistId) {
        return jdbcTemplate.query(VideoRepositorySql.GET_LIST_BY_PLAYLIST_ID, Video.getRowMapper(), playlistId);
    }

    @Override
    public Set<String> getCodeSetByMemberId(Long memberId) {
        return new HashSet<>(jdbcTemplate.query(VideoRepositorySql.GET_CODE_SET_BY_MEMBER_ID, (rs, rowNum) ->
                rs.getString("video_code"), memberId));
    }
}
