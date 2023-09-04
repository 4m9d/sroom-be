package com.m9d.sroom.lecture.repository;

import com.m9d.sroom.global.model.Playlist;
import com.m9d.sroom.global.model.Video;
import com.m9d.sroom.lecture.dto.response.RecommendLecture;
import com.m9d.sroom.lecture.dto.response.ReviewBrief;
import com.m9d.sroom.lecture.sql.LectureSqlQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.text.SimpleDateFormat;
import java.util.*;

import static com.m9d.sroom.lecture.sql.LectureSqlQuery.FIND_VIDEO_BY_ID;

@Repository
public class LectureRepository {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public LectureRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<ReviewBrief> getReviewBriefList(String lectureCode, int reviewOffset, int reviewLimit) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        return jdbcTemplate.query(LectureSqlQuery.GET_REVIEW_BRIEF_LIST_QUERY,
                (rs, rowNum) -> ReviewBrief.builder()
                        .index(rowNum + reviewOffset + 1)
                        .reviewContent(rs.getString("content"))
                        .submittedRating(rs.getInt("submitted_rating"))
                        .reviewerName(rs.getString("member_name"))
                        .publishedAt(dateFormat.format(rs.getTimestamp("submitted_date")))
                        .build(),
                lectureCode, reviewLimit, reviewOffset);
    }


    public HashSet<String> getVideosByMemberId(Long memberId) {
        return new HashSet<>(jdbcTemplate.query(LectureSqlQuery.GET_VIDEOS_BY_MEMBER_ID_QUERY, (rs, rowNum) -> rs.getString("video_code"), memberId));
    }

    public HashSet<String> getPlaylistByMemberId(Long memberId) {
        return new HashSet<>(jdbcTemplate.query(LectureSqlQuery.GET_PLAYLIST_BY_MEMBER_ID_QUERY, (rs, rowNum) -> rs.getString("playlist_code"), memberId));
    }

    public Optional<Video> findVideo(String lectureCode) {
        String query = "SELECT title, view_count, updated_at, description, thumbnail, duration, is_available, membership FROM VIDEO WHERE video_code = ?";

        Video video = queryForObjectOrNull(query,
                (rs, rowNum) -> Video.builder()
                        .title(rs.getString("title"))
                        .viewCount(rs.getLong("view_count"))
                        .updatedAt(rs.getTimestamp("updated_at"))
                        .description(rs.getString("description"))
                        .thumbnail(rs.getString("thumbnail"))
                        .duration(rs.getInt("duration"))
                        .usable(rs.getBoolean("is_available"))
                        .membership(rs.getBoolean("membership"))
                        .build(), lectureCode);
        return Optional.ofNullable(video);
    }

    public Optional<Playlist> findVideoCountAndDescription(String lectureCode) {
        String query = "SELECT video_count, updated_at, description FROM PLAYLIST WHERE playlist_code = ?";

        Playlist playlist = queryForObjectOrNull(query,
                (rs, rowNum) -> Playlist.builder()
                        .lectureCount(rs.getInt("video_count"))
                        .updatedAt(rs.getTimestamp("updated_at"))
                        .description(rs.getString("description"))
                        .build(), lectureCode);
        return Optional.ofNullable(playlist);
    }


    public List<RecommendLecture> getVideosSortedByRating() {
        return jdbcTemplate.query(LectureSqlQuery.GET_VIDEOS_SORTED_RATING, recommenVideoRowMapper(), 5);
    }

    public List<RecommendLecture> getPlaylistsSortedByRating() {
        return jdbcTemplate.query(LectureSqlQuery.GET_PLAYLISTS_SORTED_RATING, recommendPlaylistRowMapper(), 5);
    }

    public List<String> getMostEnrolledChannels(Long member_id) {
        return new ArrayList<>(jdbcTemplate.query(LectureSqlQuery.GET_MOST_ENROLLED_CHANNELS_BY_MEMBER_ID_QUERY, (rs, rowNum) -> rs.getString("channel"), member_id));
    }

    public List<RecommendLecture> getRandomVideosByChannel(String channel, int limit) {
        return jdbcTemplate.query(LectureSqlQuery.GET_RANDOM_VIDEOS_BY_CHANNEL_QUERY, recommenVideoRowMapper(), channel, limit);
    }

    public List<RecommendLecture> getRandomPlaylistsByChannel(String channel, int limit) {
        return jdbcTemplate.query(LectureSqlQuery.GET_RANDOM_PLAYLISTS_BY_CHANNEL_QUERY, recommendPlaylistRowMapper(), channel, limit);
    }

    public List<RecommendLecture> getMostViewedVideosByChannel(String channel, int limit) {
        return jdbcTemplate.query(LectureSqlQuery.GET_MOST_VIEWED_VIDEOS_BY_CHANNEL_QUERY, recommenVideoRowMapper(), channel, limit);
    }

    public List<RecommendLecture> getMostViewedPlaylistsByChannel(String channel, int limit) {
        return jdbcTemplate.query(LectureSqlQuery.GET_MOST_VIEWED_PLAYLISTS_BY_CHANNEL_QUERY, recommendPlaylistRowMapper(), channel, limit);
    }

    public List<RecommendLecture> getLatestVideosByChannel(String channel, int limit) {
        return jdbcTemplate.query(LectureSqlQuery.GET_LATEST_VIDEOS_BY_CHANNEL_QUERY, recommenVideoRowMapper(), channel, limit);
    }

    public List<RecommendLecture> getLatestPlaylistsByChannel(String channel, int limit) {
        return jdbcTemplate.query(LectureSqlQuery.GET_LATEST_PLAYLISTS_BY_CHANNEL_QUERY, recommendPlaylistRowMapper(), channel, limit);
    }

    private RowMapper<RecommendLecture> recommenVideoRowMapper() {
        return ((rs, rowNum) -> RecommendLecture.builder()
                .lectureTitle(rs.getString("title"))
                .description(rs.getString("description"))
                .channel(rs.getString("channel"))
                .lectureCode(rs.getString("video_code"))
                .rating(rs.getDouble("rating"))
                .reviewCount(rs.getInt("review_count"))
                .thumbnail(rs.getString("thumbnail"))
                .isPlaylist(false)
                .build());
    }

    private RowMapper<RecommendLecture> recommendPlaylistRowMapper() {
        return ((rs, rowNum) -> RecommendLecture.builder()
                .lectureTitle(rs.getString("title"))
                .description(rs.getString("description"))
                .channel(rs.getString("channel"))
                .lectureCode(rs.getString("playlist_code"))
                .rating(rs.getDouble("rating"))
                .reviewCount(rs.getInt("review_count"))
                .thumbnail(rs.getString("thumbnail"))
                .isPlaylist(true)
                .build());
    }

    private <T> T queryForObjectOrNull(String sql, RowMapper<T> rowMapper, Object... args) {
        try {
            return jdbcTemplate.queryForObject(sql, rowMapper, args);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public Optional<Video> findVideoById(Long videoId) {
        try {
            Video video = jdbcTemplate.queryForObject(FIND_VIDEO_BY_ID, (rs, rowNum) -> Video.builder()
                    .videoId(videoId)
                    .videoCode(rs.getString("video_code"))
                    .duration(rs.getInt("duration"))
                    .channel(rs.getString("channel"))
                    .thumbnail(rs.getString("thumbnail"))
                    .rating(rs.getDouble("accumulated_rating") / rs.getInt("review_count"))
                    .reviewCount(rs.getInt("review_count"))
                    .summaryId(rs.getLong("summary_id"))
                    .available(rs.getBoolean("is_available"))
                    .description(rs.getString("description"))
                    .chapterUse(rs.getBoolean("chapter_usage"))
                    .title(rs.getString("title"))
                    .language(rs.getString("language"))
                    .license(rs.getString("license"))
                    .updatedAt(rs.getTimestamp("updated_at"))
                    .viewCount(rs.getLong("view_count"))
                    .publishedAt(rs.getTimestamp("published_at"))
                    .membership(rs.getBoolean("membership"))
                    .build(), videoId);
            return Optional.ofNullable(video);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }
}
