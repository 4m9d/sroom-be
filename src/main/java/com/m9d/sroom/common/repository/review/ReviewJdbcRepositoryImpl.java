package com.m9d.sroom.common.repository.review;

import com.m9d.sroom.common.entity.ReviewEntity;
import com.m9d.sroom.common.repository.lecture.LectureRepositorySql;
import com.m9d.sroom.search.dto.response.ReviewBrief;
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
    public ReviewEntity save(ReviewEntity review) {
        jdbcTemplate.update(ReviewRepositorySql.SAVE,
                review.getSourceCode(),
                review.getMemberId(),
                review.getLectureId(),
                review.getSubmittedRating(),
                review.getContent());
        return getById(jdbcTemplate.queryForObject(LectureRepositorySql.GET_LAST_ID, Long.class));
    }

    @Override
    public ReviewEntity getById(Long reviewId) {
        return jdbcTemplate.queryForObject(ReviewRepositorySql.GET_BY_ID, ReviewEntity.getRowmapper(), reviewId);
    }

    @Override
    public ReviewEntity getByLectureId(Long lectureId) {
        return jdbcTemplate.queryForObject(ReviewRepositorySql.GET_BY_LECTURE_ID, ReviewEntity.getRowmapper(), lectureId);
    }

    @Override
    public List<ReviewBrief> getBriefListByCode(String lectureCode, int offset, int limit) {
        return jdbcTemplate.query(ReviewRepositorySql.GET_BRIEF_LIST_BY_CODE, ReviewBrief.getRowMapper(offset),
                lectureCode, limit, offset);
    }
}
