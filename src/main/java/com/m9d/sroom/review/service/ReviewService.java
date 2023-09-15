package com.m9d.sroom.review.service;

import com.m9d.sroom.global.mapper.Playlist;
import com.m9d.sroom.global.mapper.Video;
import com.m9d.sroom.lecture.repository.LectureRepository;
import com.m9d.sroom.review.dto.*;
import com.m9d.sroom.review.dto.LectureBrief4Review;
import com.m9d.sroom.review.dto.LectureBriefList4Review;
import com.m9d.sroom.review.dto.LectureData;
import com.m9d.sroom.global.mapper.Review;
import com.m9d.sroom.review.repository.ReviewRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final LectureRepository lectureRepository;


    public ReviewService(ReviewRepository reviewRepository, LectureRepository lectureRepository) {
        this.reviewRepository = reviewRepository;
        this.lectureRepository = lectureRepository;
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

    @Transactional
    public ReviewSubmitResponse submitReview(Long memberId, Long lectureId, ReviewSubmitRequest reviewSubmitRequest) {

        LectureData lectureData = reviewRepository.getLectureDataById(lectureId);
        String lectureCode = "";

        lectureCode = applyReview(lectureData, reviewSubmitRequest);

        Review review = Review.builder()
                .lectureId(lectureId)
                .sourceCode(lectureCode)
                .memberId(memberId)
                .content(reviewSubmitRequest.getReviewContent())
                .submittedRating(reviewSubmitRequest.getSubmittedRating())
                .build();

        Long reviewId = reviewRepository.insertReview(review);

        return ReviewSubmitResponse.builder()
                .reviewId(reviewId)
                .lectureId(lectureId)
                .submittedRating(reviewSubmitRequest.getSubmittedRating())
                .reviewContent(reviewSubmitRequest.getReviewContent())
                .build();
    }

    public LectureBrief4Review getPlaylistLectureBrief4Review(LectureData lectureData) {

        LectureBrief4Review videoCountData = reviewRepository.getVideoCountData(lectureData.getLectureId());
        LectureBrief4Review playlistData = reviewRepository.getPlaylistDataBySourceId(lectureData.getSourceId());
        int progress = (videoCountData.getCompletedVideoCount() * 100) / videoCountData.getTotalVideoCount();

        Review review = getReview(lectureData);

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

        Review review = getReview(lectureData);

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

    public String applyReview(LectureData lectureData, ReviewSubmitRequest reviewSubmitRequest) {

        String lectureCode = "";

        if (lectureData.isPlaylist()) {
            Playlist playlist = lectureRepository.getPlaylistById(lectureData.getSourceId());
            lectureCode = playlist.getPlaylistCode();
            applyReviewToPlaylist(reviewSubmitRequest, playlist);
        }
        else {
            Video video = lectureRepository.getVideoById(lectureData.getSourceId());
            lectureCode = video.getVideoCode();
            applyReviewToVideo(reviewSubmitRequest, video);
        }

        return lectureCode;
    }

    public void applyReviewToPlaylist(ReviewSubmitRequest reviewSubmitRequest, Playlist playlist) {

        playlist.setReviewCount(playlist.getReviewCount() + 1);
        playlist.setAccumulatedRating(playlist.getAccumulatedRating() + reviewSubmitRequest.getSubmittedRating());

        lectureRepository.updatePlaylist(playlist);
    }

    public void applyReviewToVideo(ReviewSubmitRequest reviewSubmitRequest, Video video) {

        video.setReviewCount(video.getReviewCount() + 1);
        video.setAccumulatedRating(video.getAccumulatedRating() + reviewSubmitRequest.getSubmittedRating());

        lectureRepository.updateVideo(video);
    }

    public Review getReview(LectureData lectureData) {
        Review review = Review.builder().build();

        review.setSubmittedRating(null);
        review.setContent(null);
        review.setSubmittedDate(null);

        if(lectureData.getIsReviewed())
            review = reviewRepository.getReviewByLectureId(lectureData.getLectureId());

        return review;
    }

    public boolean isReviewAllowed(int progress, boolean isReviewed) {
        return progress >= 70 && !isReviewed;
    }
}
