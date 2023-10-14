package com.m9d.sroom.repository.review;

import com.m9d.sroom.global.mapper.ReviewDto;
import com.m9d.sroom.lecture.dto.response.ReviewBrief;

import java.util.List;

public interface ReviewRepository {

    ReviewDto save(ReviewDto reviewDto);

    ReviewDto getById(Long reviewId);

    ReviewDto getByLectureId(Long lectureId);

    List<ReviewBrief> getBriefListByCode(String lectureCode, int reviewOffset, int reviewLimit);
}
