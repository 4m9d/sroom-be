package com.m9d.sroom.review;

import com.m9d.sroom.common.entity.jpa.*;
import com.m9d.sroom.common.entity.jpa.embedded.LearningStatus;
import com.m9d.sroom.common.repository.course.CourseJpaRepository;
import com.m9d.sroom.common.repository.lecture.LectureJpaRepository;
import com.m9d.sroom.common.repository.playlist.PlaylistJpaRepository;
import com.m9d.sroom.common.repository.review.ReviewJpaRepository;
import com.m9d.sroom.common.repository.video.VideoJpaRepository;
import com.m9d.sroom.review.dto.LectureBrief4Review;
import com.m9d.sroom.review.dto.LectureBriefList4Review;
import com.m9d.sroom.review.dto.ReviewSubmitRequest;
import com.m9d.sroom.review.dto.ReviewSubmitResponse;
import com.m9d.sroom.search.dto.response.ReviewBrief;
import com.m9d.sroom.util.ValidateUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class ReviewServiceVJpa {

    private final ReviewJpaRepository reviewRepository;

    private final CourseJpaRepository courseRepository;

    private final LectureJpaRepository lectureRepository;

    private final VideoJpaRepository videoRepository;

    private final PlaylistJpaRepository playlistRepository;


    public ReviewServiceVJpa(ReviewJpaRepository reviewRepository, CourseJpaRepository courseRepository,
                             LectureJpaRepository lectureRepository, VideoJpaRepository videoRepository,
                             PlaylistJpaRepository playlistRepository) {
        this.reviewRepository = reviewRepository;
        this.courseRepository = courseRepository;
        this.lectureRepository = lectureRepository;
        this.videoRepository = videoRepository;
        this.playlistRepository = playlistRepository;
    }

    public LectureBriefList4Review getLectureList(Long memberId, Long courseId) {

        CourseEntity course = courseRepository.getById(courseId);
        List<LectureEntity> lectureList = course.getLectures();
        List<LectureBrief4Review> lectures = new ArrayList<>();

        for (LectureEntity lecture : lectureList) {
            if (lecture.getIsPlaylist()) {
                lectures.add(getPlaylistLectureBrief4Review(lecture));
            } else {
                lectures.add(getVideoLectureBrief4Review(lecture));
            }
        }

        return LectureBriefList4Review.builder()
                .lectures(lectures)
                .build();
    }

    @Transactional
    public ReviewSubmitResponse submitReview(Long memberId, Long lectureId, ReviewSubmitRequest reviewSubmitRequest) {

        LectureEntity lecture = lectureRepository.getById(lectureId);
        String lectureCode;

        lectureCode = applyReview(lecture, reviewSubmitRequest);

        ReviewEntity review = ReviewEntity.create(lecture, lectureCode, reviewSubmitRequest.getSubmittedRating(), reviewSubmitRequest.getReviewContent());

        Long reviewId = reviewRepository.save(review).getReviewId();

        log.info("subject = reviewSaved, isPlaylist = {}, grade = {}", ValidateUtil.checkIfPlaylist(lectureCode),
                reviewSubmitRequest.getSubmittedRating());

        return ReviewSubmitResponse.builder()
                .reviewId(reviewId)
                .lectureId(lectureId)
                .submittedRating(reviewSubmitRequest.getSubmittedRating())
                .reviewContent(reviewSubmitRequest.getReviewContent())
                .build();
    }

    LectureBrief4Review getPlaylistLectureBrief4Review(LectureEntity lecture) {

        LectureBrief4Review videoCountData = getVideoCountData(lecture);
        PlaylistEntity playlist = playlistRepository.getById(lecture.getSourceId());
        int progress = (videoCountData.getCompletedVideoCount() * 100) / videoCountData.getTotalVideoCount();

        String content = null;
        Integer submittedRating = null;
        String submittedDate = null;

        if (lecture.getIsReviewed()) {
            content = lecture.getReview().getContent();
            submittedRating = lecture.getReview().getSubmittedRating();
            submittedDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(lecture.getReview().getSubmittedDate());
        }

        return LectureBrief4Review.builder()
                .index(lecture.getLectureIndex())
                .lectureId(lecture.getLectureId())
                .title(playlist.getContentInfo().getTitle())
                .thumbnail(playlist.getContentInfo().getThumbnail())
                .channel(lecture.getChannel())
                .isPlaylist(true)
                .totalVideoCount(videoCountData.getTotalVideoCount())
                .completedVideoCount(videoCountData.getCompletedVideoCount())
                .progress(progress)
                .content(content)
                .rating(submittedRating)
                .submittedAt(submittedDate)
                .isReviewAllowed(isReviewAllowed(progress, lecture.getIsReviewed()))
                .build();
    }

    LectureBrief4Review getVideoLectureBrief4Review(LectureEntity lecture) {
        CourseVideoEntity courseVideo = lecture.getCourseVideos()
                .get(0);

        VideoEntity video = videoRepository.getById(lecture.getSourceId());
        int progress = (courseVideo.getStatus().getMaxDuration() * 100) / video.getContentInfo().getDuration();

        if (progress < 50 && courseVideo.getStatus().getIsComplete())
            progress = 100;

        String content = null;
        Integer submittedRating = null;
        String submittedDate = null;

        if (lecture.getIsReviewed()) {
            content = lecture.getReview().getContent();
            submittedRating = lecture.getReview().getSubmittedRating();
            submittedDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(lecture.getReview().getSubmittedDate());
        }

        return LectureBrief4Review.builder()
                .index(lecture.getLectureIndex())
                .lectureId(lecture.getLectureId())
                .title(video.getContentInfo().getTitle())
                .thumbnail(video.getContentInfo().getThumbnail())
                .channel(lecture.getChannel())
                .isPlaylist(false)
                .viewDuration(courseVideo.getStatus().getMaxDuration())
                .lectureDuration(video.getContentInfo().getDuration())
                .progress(progress)
                .content(content)
                .rating(submittedRating)
                .submittedAt(submittedDate)
                .isReviewAllowed(isReviewAllowed(progress, lecture.getIsReviewed()))
                .build();

    }

    String applyReview(LectureEntity lecture, ReviewSubmitRequest reviewSubmitRequest) {

        String lectureCode = "";

        if (lecture.getIsPlaylist()) {
            PlaylistEntity playlist = playlistRepository.getById(lecture.getSourceId());
            lectureCode = playlist.getPlaylistCode();
            playlist.getReview().updateReview(reviewSubmitRequest.getSubmittedRating());
        } else {
            VideoEntity video = videoRepository.getById(lecture.getSourceId());
            lectureCode = video.getVideoCode();
            video.getReview().updateReview(reviewSubmitRequest.getSubmittedRating());
        }

        lecture.updateIsReviewed();

        return lectureCode;
    }

    boolean isReviewAllowed(int progress, boolean isReviewed) {
        return progress >= 50 && !isReviewed;
    }

    LectureBrief4Review getVideoCountData(LectureEntity lecture) {
        List<CourseVideoEntity> courseVideoList = lecture.getCourseVideos();
        int completedVideoCount = (int) courseVideoList.stream()
                .map(CourseVideoEntity::getStatus)
                .filter(LearningStatus::getIsComplete)
                .count();

        return LectureBrief4Review.builder()
                .totalVideoCount(courseVideoList.size())
                .completedVideoCount(completedVideoCount)
                .build();
    }

    public List<ReviewBrief> getReviewInfo(String videoCode, int offset, int limit) {
        List<ReviewEntity> reviews = reviewRepository.getListByCode(videoCode, offset, limit);
        List<ReviewBrief> reviewBriefs = new ArrayList<>();

        for (ReviewEntity review: reviews) {
            reviewBriefs.add(ReviewMapper.getBriefByEntity(review, offset + reviews.indexOf(review)));
        }

        return reviewBriefs;
    }

    @Transactional
    public void updateRating() {
        int updateVideoCount = videoRepository.updateRating();
        int updatePlaylistCount = playlistRepository.updateRating();
        log.info("subject = UpdateRating, updatedVideoCount = {}, updatedPlaylistCount = {}", updateVideoCount, updatePlaylistCount);
    }
}
