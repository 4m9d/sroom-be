package com.m9d.sroom.lecture.repository;

import com.m9d.sroom.lecture.dto.response.ReviewBrief;
import com.m9d.sroom.lecture.sql.LectureSqlQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.text.SimpleDateFormat;
import java.util.*;

import static com.m9d.sroom.lecture.sql.LectureSqlQuery.*;

@Repository
public class LectureRepository {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public LectureRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<ReviewBrief> getReviewBriefList(String lectureCode, int reviewOffset, int reviewLimit) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        List<ReviewBrief> reviewBriefList = jdbcTemplate.query(GET_REVIEW_BRIEF_LIST_QUERY,
                (rs, rowNum) -> ReviewBrief.builder()
                        .index(rowNum + reviewOffset + 1)
                        .reviewContent(rs.getString("content"))
                        .submittedRating(rs.getInt("submitted_rating"))
                        .reviewerName(rs.getString("member_name"))
                        .publishedAt(dateFormat.format(rs.getTimestamp("submitted_date")))
                        .build(),
                lectureCode, reviewLimit, reviewOffset);

        return reviewBriefList;
    }


    public HashSet<String> getVideosByMemberId(Long memberId) {
        Set<String> videoSet = new HashSet<>(jdbcTemplate.query(GET_VIDEOS_BY_MEMBER_ID_QUERY, (rs, rowNum) -> rs.getString("video_code"), memberId));

        return new HashSet<>(videoSet);
    }

    public HashSet<String> getPlaylistByMemberId(Long memberId) {
        Set<String> playlistSet = new HashSet<>(jdbcTemplate.query(GET_PLAYLIST_BY_MEMBER_ID_QUERY, (rs, rowNum) -> rs.getString("playlist_code"), memberId));

        return new HashSet<>(playlistSet);
    }
}
