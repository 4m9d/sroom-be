package com.m9d.sroom.repository.review;

import com.m9d.sroom.global.mapper.Review;

import java.util.List;

public interface ReviewRepository {

    Review save(Review review);

    Review getById(Long reviewId);

    List<Review> getListByCode(String lectureCode, int offset, int limit);

    Review getByLectureId(Long lectureId);
}
