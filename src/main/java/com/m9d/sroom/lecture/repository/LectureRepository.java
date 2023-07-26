package com.m9d.sroom.lecture.repository;

import com.m9d.sroom.lecture.dto.response.ReviewBrief;
import com.m9d.sroom.lecture.sql.LectureSqlQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
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
}
