package com.m9d.sroom.lecture.repository;

import com.m9d.sroom.course.domain.Playlist;
import com.m9d.sroom.course.domain.Video;
import com.m9d.sroom.lecture.dto.response.ReviewBrief;
import com.m9d.sroom.lecture.sql.LectureSqlQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.text.SimpleDateFormat;
import java.util.*;

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

    public Optional<Video> findViewCount(String lectureCode) {
        String query = "SELECT view_count, updated_at FROM VIDEO WHERE video_code = ?";

        Video video = queryForObjectOrNull(query,
                (rs, rowNum) -> Video.builder()
                        .viewCount(rs.getLong("view_count"))
                        .updatedAt(rs.getTimestamp("updated_at"))
                        .build(), lectureCode);
        return Optional.ofNullable(video);
    }

    public Optional<Playlist> findVideoCount(String lectureCode) {
        String query = "SELECT video_count, updated_at FROM PLAYLIST WHERE playlist_code = ?";

        Playlist playlist = queryForObjectOrNull(query,
                (rs, rowNum) -> Playlist.builder()
                        .lectureCount(rs.getInt("video_count"))
                        .updatedAt(rs.getTimestamp("updated_at"))
                        .build(), lectureCode);
        return Optional.ofNullable(playlist);
    }

    private <T> T queryForObjectOrNull(String sql, RowMapper<T> rowMapper, Object... args) {
        try {
            return jdbcTemplate.queryForObject(sql, rowMapper, args);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }
}
