package com.m9d.sroom.common.repository.review;

import com.m9d.sroom.common.dto.Review;
import com.m9d.sroom.common.repository.lecture.LectureRepositorySql;
import com.m9d.sroom.lecture.dto.response.ReviewBrief;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ReviewJdbcRepositoryImpl implements ReviewRepository {

    JdbcTemplate jdbcTemplate;

    public ReviewJdbcRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Review save(Review review) {
        jdbcTemplate.update(ReviewRepositorySql.SAVE,
                review.getSourceCode(),
                review.getMemberId(),
                review.getLectureId(),
                review.getSubmittedRating(),
                review.getContent(),
                review.getSubmittedDate());
        return getById(jdbcTemplate.queryForObject(LectureRepositorySql.GET_LAST_ID, Long.class));
    }

    @Override
    public Review getById(Long reviewId) {
        return jdbcTemplate.queryForObject(ReviewRepositorySql.GET_BY_ID, Review.getRowmapper(), reviewId);
    }

    @Override
    public Review getByLectureId(Long lectureId) {
        return jdbcTemplate.queryForObject(ReviewRepositorySql.GET_BY_LECTURE_ID, Review.getRowmapper(), lectureId);
    }

    @Override
    public List<ReviewBrief> getBriefListByCode(String lectureCode, int offset, int limit) {
        return jdbcTemplate.query(ReviewRepositorySql.GET_BRIEF_LIST_BY_CODE, ReviewBrief.getRowMapper(offset),
                lectureCode, limit, offset);
    }
}
