package com.m9d.sroom.lecture;

import com.m9d.sroom.common.repository.review.ReviewRepository;
import com.m9d.sroom.course.CourseVideo;
import com.m9d.sroom.lecture.dto.response.ReviewBrief;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LectureServiceHelper {
    private final ReviewRepository reviewRepository;

    public LectureServiceHelper(ReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
    }

    public List<ReviewBrief> getReviewInfo(String videoCode, int offset, int limit) {
        return reviewRepository.getBriefListByCode(videoCode, offset, limit);
    }
}
