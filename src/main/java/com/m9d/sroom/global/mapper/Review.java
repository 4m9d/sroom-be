package com.m9d.sroom.global.mapper;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import org.springframework.jdbc.core.RowMapper;

import java.sql.Timestamp;

@Data
@Builder
@Schema(description = "각 강의에 대한 리뷰 데이터")
public class Review {

    private Long reviewId;

    private String sourceCode;

    private Long memberId;

    private Long lectureId;

    private String content;

    private Integer submittedRating;

    private Timestamp submittedDate;

    public static RowMapper<Review> getRowmapper() {
        return (rs, rowNum) -> Review.builder()
                .reviewId(rs.getLong("review_id"))
                .sourceCode(rs.getString("source_code"))
                .memberId(rs.getLong("member_id"))
                .lectureId(rs.getLong("lecture_id"))
                .submittedRating(rs.getInt("submitted_rating"))
                .content(rs.getString("content"))
                .submittedDate(rs.getTimestamp("submitted_date"))
                .build();
    }
}
