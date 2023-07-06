package com.m9d.sroom.lecture.repository;

import com.m9d.sroom.lecture.domain.ReviewBrief;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class LectureRepository {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public LectureRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<ReviewBrief> getReviewBriefList(String lectureCode, int reviewOffset, int reviewLimit) {
        String sql = "SELECT id, content, submitted_rating FROM REVIEW WHERE lecture_code = ? ORDER BY submitted_date DESC LIMIT ? OFFSET ?";
        List<ReviewBrief> reviewBriefList = jdbcTemplate.query(sql, new Object[]{lectureCode, reviewLimit, reviewOffset},
                (rs, rowNum) -> ReviewBrief.builder()
                        .index(rowNum + reviewOffset)
                        .reviewContent(rs.getString("content"))
                        .submittedRating(rs.getInt("submitted_rating"))
                        .build()
        );
        return reviewBriefList;
    }
}
