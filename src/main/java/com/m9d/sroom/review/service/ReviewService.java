package com.m9d.sroom.review.service;

import com.m9d.sroom.global.model.Playlist;
import com.m9d.sroom.global.model.Video;
import com.m9d.sroom.lecture.repository.LectureRepository;
import com.m9d.sroom.review.dto.*;
import com.m9d.sroom.review.repository.ReviewRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
    public ReviewSubmitResponse reviewSubmit(Long memberId, Long lectureId, ReviewSubmitRequest reviewSubmitRequest) {

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

    public String applyReview(LectureData lectureData, ReviewSubmitRequest reviewSubmitRequest) {

        String lectureCode = "";

        if (lectureData.isPlaylist()) {
            lectureCode = reviewRepository.getPlaylistCodeByLectureId(lectureData.getLectureId());
            applyReviewToPlaylist(lectureData.getSourceId(), reviewSubmitRequest);
        }
        else {
            lectureCode = reviewRepository.getVideoCodeByLectureId(lectureData.getLectureId());
            applyReviewToVideo(lectureData.getSourceId(), reviewSubmitRequest);
        }

        return lectureCode;
    }

    public void applyReviewToPlaylist(Long sourceId, ReviewSubmitRequest reviewSubmitRequest) {
        Optional<Playlist> playlistOptional = lectureRepository.findPlaylistById(sourceId);
        Playlist playlist = playlistOptional.get();

        int accumulatedRating = playlist.getAccumulatedRating();
        int reviewCount = playlist.getReviewCount();

        playlist.setReviewCount(reviewCount + 1);
        playlist.setAccumulatedRating(accumulatedRating + reviewSubmitRequest.getSubmittedRating());

        lectureRepository.updatePlaylist(playlist);
    }

    public void applyReviewToVideo(Long sourceId, ReviewSubmitRequest reviewSubmitRequest) {
        Optional<Video> videoOptional = lectureRepository.findVideoById(sourceId);
        Video video = videoOptional.get();

        int accumulatedRating = video.getAccumulatedRating();
        int reviewCount = video.getReviewCount();

        video.setReviewCount(reviewCount + 1);
        video.setAccumulatedRating(accumulatedRating + reviewSubmitRequest.getSubmittedRating());

        lectureRepository.updateVideo(video);
    }

    public boolean isReviewAllowed(int progress, boolean isReviewed) {
        return progress >= 70 && !isReviewed;
    }
}
