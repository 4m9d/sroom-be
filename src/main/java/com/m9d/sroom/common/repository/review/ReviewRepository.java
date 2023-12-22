package com.m9d.sroom.common.repository.review;

import com.m9d.sroom.common.entity.jdbctemplate.ReviewEntity;
import com.m9d.sroom.search.dto.response.ReviewBrief;

import java.util.List;

public interface ReviewRepository {

    ReviewEntity save(ReviewEntity review);

    ReviewEntity getById(Long reviewId);

    ReviewEntity getByLectureId(Long lectureId);

    List<ReviewBrief> getBriefListByCode(String lectureCode, int reviewOffset, int reviewLimit);
}
