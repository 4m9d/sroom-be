package com.m9d.sroom.review.service;

import com.m9d.sroom.review.dto.LectureBrief4Review;
import com.m9d.sroom.review.dto.LectureBriefList4Review;
import com.m9d.sroom.review.dto.LectureData;
import com.m9d.sroom.global.mapper.Review;
import com.m9d.sroom.review.repository.ReviewRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class ReviewService {

    private final ReviewRepository reviewRepository;


    public ReviewService(ReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
    }

    public LectureBriefList4Review getLectureList(Long memberId, Long courseId) {

        List<LectureData> lectureList = reviewRepository.getLectureDataListByCourseId(courseId);
        List<LectureBrief4Review> lectures = new ArrayList<>();

        for(LectureData lectureData : lectureList) {
            if(lectureData.isPlaylist()) {
                lectures.add(getPlaylistLectureBrief4Review(lectureData));
            }
            else {
                lectures.add(getVideoLectureBrief4Review(lectureData));
            }
        }

        return LectureBriefList4Review.builder()
                .lectures(lectures)
                .build();
    }

    public LectureBrief4Review getPlaylistLectureBrief4Review(LectureData lectureData) {

        LectureBrief4Review videoCountData = reviewRepository.getVideoCountData(lectureData.getLectureId());
        LectureBrief4Review playlistData = reviewRepository.getPlaylistDataBySourceId(lectureData.getSourceId());
        int progress = (videoCountData.getCompletedVideoCount() * 100) / videoCountData.getTotalVideoCount();

        Review review = Review.builder().build();

        review.setSubmittedRating(null);
        review.setContent(null);
        review.setSubmittedDate(null);

        if(lectureData.getIsReviewed())
            review = reviewRepository.getReviewByLectureId(lectureData.getLectureId());

        return LectureBrief4Review.builder()
                .index(lectureData.getLectureIndex())
                .lectureId(lectureData.getLectureId())
                .title(playlistData.getTitle())
                .thumbnail(playlistData.getThumbnail())
                .channel(lectureData.getChannel())
                .isPlaylist(true)
                .totalVideoCount(videoCountData.getTotalVideoCount())
                .completedVideoCount(videoCountData.getCompletedVideoCount())
                .progress(progress)
                .content(review.getContent())
                .rating(review.getSubmittedRating())
                .submittedAt(review.getSubmittedDate())
                .isReviewAllowed(isReviewAllowed(progress, lectureData.getIsReviewed()))
                .build();
    }

    public LectureBrief4Review getVideoLectureBrief4Review(LectureData lectureData) {

        int viewDuration = reviewRepository.getViewDurationByLectureId(lectureData.getLectureId());
        LectureBrief4Review videoData = reviewRepository.getVideoDataBySourceId(lectureData.getSourceId());
        int progress = (viewDuration * 100) / videoData.getLectureDuration();

        Review review = Review.builder().build();

        review.setSubmittedRating(null);
        review.setContent(null);
        review.setSubmittedDate(null);

        if(lectureData.getIsReviewed())
            review = reviewRepository.getReviewByLectureId(lectureData.getLectureId());

        return LectureBrief4Review.builder()
                .index(lectureData.getLectureIndex())
                .lectureId(lectureData.getLectureId())
                .title(videoData.getTitle())
                .thumbnail(videoData.getThumbnail())
                .channel(lectureData.getChannel())
                .isPlaylist(false)
                .viewDuration(viewDuration)
                .lectureDuration(videoData.getLectureDuration())
                .progress(progress)
                .content(review.getContent())
                .rating(review.getSubmittedRating())
                .submittedAt(review.getSubmittedDate())
                .isReviewAllowed(isReviewAllowed(progress, lectureData.getIsReviewed()))
                .build();

    }

    public boolean isReviewAllowed(int progress, boolean isReviewed) {
        return progress >= 70 && !isReviewed;
    }
}
