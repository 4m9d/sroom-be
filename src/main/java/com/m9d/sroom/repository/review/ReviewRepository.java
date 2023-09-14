package com.m9d.sroom.repository.review;

import com.m9d.sroom.global.model.Review;
import com.m9d.sroom.lecture.dto.response.ReviewBrief;

import java.util.List;

public interface ReviewRepository {

    List<Review> getListByCode(String lectureCode, int offset, int limit);


}
