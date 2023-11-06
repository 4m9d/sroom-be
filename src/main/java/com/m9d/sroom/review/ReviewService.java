package com.m9d.sroom.review;

import com.m9d.sroom.common.entity.*;
import com.m9d.sroom.common.repository.coursevideo.CourseVideoRepository;
import com.m9d.sroom.common.repository.lecture.LectureRepository;
import com.m9d.sroom.common.repository.playlist.PlaylistRepository;
import com.m9d.sroom.common.repository.review.ReviewRepository;
import com.m9d.sroom.common.repository.video.VideoRepository;
import com.m9d.sroom.search.dto.response.ReviewBrief;
import com.m9d.sroom.review.dto.*;
import com.m9d.sroom.util.ValidateUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final LectureRepository lectureRepository;
    private final PlaylistRepository playlistRepository;
    private final VideoRepository videoRepository;
    private final CourseVideoRepository courseVideoRepository;


    public ReviewService(ReviewRepository reviewRepository, LectureRepository lectureRepository,
                         PlaylistRepository playlistRepository, VideoRepository videoRepository,
                         CourseVideoRepository courseVideoRepository) {
        this.reviewRepository = reviewRepository;
        this.lectureRepository = lectureRepository;
        this.playlistRepository = playlistRepository;
        this.videoRepository = videoRepository;
        this.courseVideoRepository = courseVideoRepository;
    }

    public LectureBriefList4Review getLectureList(Long memberId, Long courseId) {

        List<LectureEntity> lectureList = lectureRepository.getListByCourseId(courseId);
        List<LectureBrief4Review> lectures = new ArrayList<>();

        for (LectureEntity lecture : lectureList) {
            if (lecture.getPlaylist()) {
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
        String lectureCode = "";

        lectureCode = applyReview(lecture, reviewSubmitRequest);

        ReviewEntity review = ReviewEntity.builder()
                .lectureId(lectureId)
                .sourceCode(lectureCode)
                .memberId(memberId)
                .content(reviewSubmitRequest.getReviewContent())
                .submittedRating(reviewSubmitRequest.getSubmittedRating())
                .build();

        Long reviewId = reviewRepository.save(review).getReviewId();

        log.info("review saved. isPlaylist = {}, grade = {}", ValidateUtil.checkIfPlaylist(lectureCode),
                reviewSubmitRequest.getSubmittedRating());

        return ReviewSubmitResponse.builder()
                .reviewId(reviewId)
                .lectureId(lectureId)
                .submittedRating(reviewSubmitRequest.getSubmittedRating())
                .reviewContent(reviewSubmitRequest.getReviewContent())
                .build();
    }

    LectureBrief4Review getPlaylistLectureBrief4Review(LectureEntity lecture) {

        LectureBrief4Review videoCountData = getVideoCountData(lecture.getId());
        PlaylistEntity playlist = playlistRepository.getById(lecture.getSourceId());
        int progress = (videoCountData.getCompletedVideoCount() * 100) / videoCountData.getTotalVideoCount();

        ReviewEntity review = getReview(lecture);
        String submittedDate;

        if (review.getSubmittedDate() != null) {
            submittedDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(review.getSubmittedDate());
        } else {
            submittedDate = null;
        }

        return LectureBrief4Review.builder()
                .index(lecture.getLectureIndex())
                .lectureId(lecture.getId())
                .title(playlist.getTitle())
                .thumbnail(playlist.getThumbnail())
                .channel(lecture.getChannel())
                .isPlaylist(true)
                .totalVideoCount(videoCountData.getTotalVideoCount())
                .completedVideoCount(videoCountData.getCompletedVideoCount())
                .progress(progress)
                .content(review.getContent())
                .rating(review.getSubmittedRating())
                .submittedAt(submittedDate)
                .isReviewAllowed(isReviewAllowed(progress, lecture.getReviewed()))
                .build();
    }

    LectureBrief4Review getVideoLectureBrief4Review(LectureEntity lecture) {

        CourseVideoEntity courseVideo = courseVideoRepository.getListByLectureId(lecture.getId())
                .get(0);
        VideoEntity video = videoRepository.getById(lecture.getSourceId());
        int progress = (courseVideo.getMaxDuration() * 100) / video.getDuration();

        if (progress < 50 && courseVideo.isComplete())
            progress = 100;

        ReviewEntity review = getReview(lecture);
        String submittedDate;

        if (review.getSubmittedDate() != null) {
            submittedDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(review.getSubmittedDate());
        } else {
            submittedDate = null;
        }

        return LectureBrief4Review.builder()
                .index(lecture.getLectureIndex())
                .lectureId(lecture.getId())
                .title(video.getTitle())
                .thumbnail(video.getThumbnail())
                .channel(lecture.getChannel())
                .isPlaylist(false)
                .viewDuration(courseVideo.getMaxDuration())
                .lectureDuration(video.getDuration())
                .progress(progress)
                .content(review.getContent())
                .rating(review.getSubmittedRating())
                .submittedAt(submittedDate)
                .isReviewAllowed(isReviewAllowed(progress, lecture.getReviewed()))
                .build();

    }

    String applyReview(LectureEntity lecture, ReviewSubmitRequest reviewSubmitRequest) {

        String lectureCode = "";

        if (lecture.getPlaylist()) {
            PlaylistEntity playlist = playlistRepository.getById(lecture.getSourceId());
            lectureCode = playlist.getPlaylistCode();
            applyReviewToPlaylist(reviewSubmitRequest, playlist);
        } else {
            VideoEntity video = videoRepository.getById(lecture.getSourceId());
            lectureCode = video.getVideoCode();
            applyReviewToVideo(reviewSubmitRequest, video);
        }

        lecture.setReviewed(true);

        lectureRepository.updateById(lecture.getId(), lecture);

        return lectureCode;
    }

    void applyReviewToPlaylist(ReviewSubmitRequest reviewSubmitRequest, PlaylistEntity playlist) {

        playlist.setReviewCount(playlist.getReviewCount() + 1);
        playlist.setAccumulatedRating(playlist.getAccumulatedRating() + reviewSubmitRequest.getSubmittedRating());

        playlistRepository.updateById(playlist.getPlaylistId(), playlist);
    }

    void applyReviewToVideo(ReviewSubmitRequest reviewSubmitRequest, VideoEntity video) {

        video.setReviewCount(video.getReviewCount() + 1);
        video.setAccumulatedRating(video.getAccumulatedRating() + reviewSubmitRequest.getSubmittedRating());

        videoRepository.updateById(video.getVideoId(), video);
    }

    ReviewEntity getReview(LectureEntity lecture) {
        ReviewEntity review = ReviewEntity.builder().build();

        review.setSubmittedRating(null);
        review.setContent(null);
        review.setSubmittedDate(null);

        if (lecture.getReviewed())
            review = reviewRepository.getByLectureId(lecture.getId());

        return review;
    }

    boolean isReviewAllowed(int progress, boolean isReviewed) {
        return progress >= 50 && !isReviewed;
    }

    LectureBrief4Review getVideoCountData(Long lectureId) {
        List<CourseVideoEntity> courseVideoList = courseVideoRepository.getListByLectureId(lectureId);
        int completedVideoCount = (int) courseVideoList.stream()
                .filter(CourseVideoEntity::isComplete)
                .count();

        return LectureBrief4Review.builder()
                .totalVideoCount(courseVideoList.size())
                .completedVideoCount(completedVideoCount)
                .build();
    }

    public List<ReviewBrief> getReviewInfo(String videoCode, int offset, int limit) {
        return reviewRepository.getBriefListByCode(videoCode, offset, limit);
    }

    @Transactional
    public void updateRating() {
        int updateVideoCount = videoRepository.updateRating();
        int updatePlaylistCount = playlistRepository.updateRating();
        log.info("Update_Rating : updated_video_count = {}, updated_playlist_count = {}", updateVideoCount, updatePlaylistCount);
    }
}
