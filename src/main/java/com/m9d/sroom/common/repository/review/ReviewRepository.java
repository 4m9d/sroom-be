package com.m9d.sroom.common.repository.review;

import com.m9d.sroom.common.dto.Review;
import com.m9d.sroom.lecture.dto.response.ReviewBrief;

import java.util.List;

public interface ReviewRepository {

    Review save(Review review);

    Review getById(Long reviewId);

    Review getByLectureId(Long lectureId);

    List<ReviewBrief> getBriefListByCode(String lectureCode, int reviewOffset, int reviewLimit);
}
