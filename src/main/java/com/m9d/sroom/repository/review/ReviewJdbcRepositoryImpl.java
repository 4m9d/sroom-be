package com.m9d.sroom.repository.review;

import com.m9d.sroom.global.model.Review;
import com.m9d.sroom.lecture.dto.response.ReviewBrief;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ReviewJdbcRepositoryImpl implements ReviewRepository{
    @Override
    public List<Review> getListByCode(String lectureCode, int offset, int limit) {
        return null;
    }
}
