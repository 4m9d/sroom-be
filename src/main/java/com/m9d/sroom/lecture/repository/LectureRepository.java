package com.m9d.sroom.lecture.repository;

import com.m9d.sroom.lecture.dto.response.ReviewBrief;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class LectureRepository {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public LectureRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<ReviewBrief> getReviewBriefList(String lectureCode, int reviewOffset, int reviewLimit) {
        String sql = "SELECT review_id, content, submitted_rating FROM REVIEW WHERE source_code = ? ORDER BY submitted_date DESC LIMIT ? OFFSET ?";
        List<ReviewBrief> reviewBriefList = jdbcTemplate.query(sql, new Object[]{lectureCode, reviewLimit, reviewOffset},
                (rs, rowNum) -> ReviewBrief.builder()
                        .index(rowNum + reviewOffset)
                        .reviewContent(rs.getString("content"))
                        .submittedRating(rs.getInt("submitted_rating"))
                        .build()
        );
        return reviewBriefList;
    }

    public HashSet<String> getVideosByMemberId(Long memberId) {
        String sql = "SELECT v.video_code FROM COURSEVIDEO cv JOIN VIDEO v ON cv.video_id = v.video_id WHERE cv.member_id = ?";

        Set<String> videoSet = new HashSet<>(jdbcTemplate.query(sql, (rs, rowNum) -> rs.getString("video_code"), memberId));

        return new HashSet<>(videoSet);
    }

    public HashSet<String> getPlaylistByMemberId(Long memberId) {
        String sql = "SELECT p.playlist_code FROM LECTURE l JOIN PLAYLIST p ON l.source_id = p.playlist_id WHERE l.member_id = ? AND l.is_playlist = true";

        Set<String> playlistSet = new HashSet<>(jdbcTemplate.query(sql, (rs, rowNum) -> rs.getString("playlist_code"), memberId));

        return new HashSet<>(playlistSet);
    }
}
