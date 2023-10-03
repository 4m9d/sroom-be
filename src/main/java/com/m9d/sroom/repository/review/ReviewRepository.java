package com.m9d.sroom.repository.review;

import com.m9d.sroom.global.mapper.Review;
import com.m9d.sroom.lecture.dto.response.ReviewBrief;

import java.util.List;

public interface ReviewRepository {

    Review save(Review review);

    Review getById(Long reviewId);

    Review getByLectureId(Long lectureId);

    List<ReviewBrief> getBriefListByCode(String lectureCode, int reviewOffset, int reviewLimit);
}
