package com.m9d.sroom.review.repository;

import com.m9d.sroom.review.ReviewDto;
import com.m9d.sroom.lecture.dto.response.ReviewBrief;
import com.m9d.sroom.common.repository.lecture.LectureRepositorySql;
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
    public ReviewDto save(ReviewDto reviewDto) {
        jdbcTemplate.update(ReviewRepositorySql.SAVE,
                reviewDto.getSourceCode(),
                reviewDto.getMemberId(),
                reviewDto.getLectureId(),
                reviewDto.getSubmittedRating(),
                reviewDto.getContent(),
                reviewDto.getSubmittedDate());
        return getById(jdbcTemplate.queryForObject(LectureRepositorySql.GET_LAST_ID, Long.class));
    }

    @Override
    public ReviewDto getById(Long reviewId) {
        return jdbcTemplate.queryForObject(ReviewRepositorySql.GET_BY_ID, ReviewDto.getRowmapper(), reviewId);
    }

    @Override
    public ReviewDto getByLectureId(Long lectureId) {
        return jdbcTemplate.queryForObject(ReviewRepositorySql.GET_BY_LECTURE_ID, ReviewDto.getRowmapper(), lectureId);
    }

    @Override
    public List<ReviewBrief> getBriefListByCode(String lectureCode, int offset, int limit) {
        return jdbcTemplate.query(ReviewRepositorySql.GET_BRIEF_LIST_BY_CODE, ReviewBrief.getRowMapper(offset),
                lectureCode, limit, offset);
    }
}
