package com.m9d.sroom.repository.review;

import com.m9d.sroom.lecture.dto.response.ReviewBrief;

import java.util.List;

public interface ReviewRepository {

    List<ReviewBrief> getBriefListByCode(String lectureCode, int offset, int limit);


}
