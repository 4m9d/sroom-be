package com.m9d.sroom.repository.review;

import com.m9d.sroom.global.mapper.Review;
import com.m9d.sroom.repository.lecture.LectureRepositorySql;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.JDBCType;
import java.util.List;

@Repository
public class ReviewJdbcRepositoryImpl implements ReviewRepository{

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
    public List<Review> getListByCode(String lectureCode, int offset, int limit) {
        return null;
    }

    @Override
    public Review getByLectureId(Long lectureId) {
        return jdbcTemplate.queryForObject(ReviewRepositorySql.GET_BY_LECTURE_ID, Review.getRowmapper(), lectureId);
    }
}
