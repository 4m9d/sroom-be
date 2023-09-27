package com.m9d.sroom.review.service;

import com.m9d.sroom.global.mapper.*;
import com.m9d.sroom.repository.coursevideo.CourseVideoRepository;
import com.m9d.sroom.repository.lecture.LectureRepository;
import com.m9d.sroom.repository.playlist.PlaylistRepository;
import com.m9d.sroom.repository.review.ReviewRepository;
import com.m9d.sroom.repository.video.VideoRepository;
import com.m9d.sroom.review.dto.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class ReviewService {

    private final com.m9d.sroom.repository.review.ReviewRepository reviewRepository;
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

        List<Lecture> lectureList = lectureRepository.getListByCourseId(courseId);
        List<LectureBrief4Review> lectures = new ArrayList<>();

        for(Lecture lecture : lectureList) {
            if(lecture.getPlaylist()) {
                lectures.add(getPlaylistLectureBrief4Review(lecture));
            }
            else {
                lectures.add(getVideoLectureBrief4Review(lecture));
            }
        }

        return LectureBriefList4Review.builder()
                .lectures(lectures)
                .build();
    }

    @Transactional
    public ReviewSubmitResponse submitReview(Long memberId, Long lectureId, ReviewSubmitRequest reviewSubmitRequest) {

        Lecture lecture = lectureRepository.getById(lectureId);
        String lectureCode = "";

        lectureCode = applyReview(lecture, reviewSubmitRequest);

        Review review = Review.builder()
                .lectureId(lectureId)
                .sourceCode(lectureCode)
                .memberId(memberId)
                .content(reviewSubmitRequest.getReviewContent())
                .submittedRating(reviewSubmitRequest.getSubmittedRating())
                .build();

        Long reviewId = reviewRepository.save(review).getReviewId();

        return ReviewSubmitResponse.builder()
                .reviewId(reviewId)
                .lectureId(lectureId)
                .submittedRating(reviewSubmitRequest.getSubmittedRating())
                .reviewContent(reviewSubmitRequest.getReviewContent())
                .build();
    }

    LectureBrief4Review getPlaylistLectureBrief4Review(Lecture lecture) {

        LectureBrief4Review videoCountData = getVideoCountData(lecture.getId());
        Playlist playlist = playlistRepository.getById(lecture.getSourceId());
        int progress = (videoCountData.getCompletedVideoCount() * 100) / videoCountData.getTotalVideoCount();

        Review review = getReview(lecture);

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
                .submittedAt((new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(review.getSubmittedDate())))
                .isReviewAllowed(isReviewAllowed(progress, lecture.getReviewed()))
                .build();
    }

    LectureBrief4Review getVideoLectureBrief4Review(Lecture lecture) {

        int viewDuration = courseVideoRepository.getListByLectureId(lecture.getId())
                .get(0)
                .getMaxDuration();
        Video video = videoRepository.getById(lecture.getSourceId());
        int progress = (viewDuration * 100) / video.getDuration();

        Review review = getReview(lecture);

        if(lecture.getReviewed())
            review = reviewRepository.getByLectureId(lecture.getId());

        return LectureBrief4Review.builder()
                .index(lecture.getLectureIndex())
                .lectureId(lecture.getId())
                .title(video.getTitle())
                .thumbnail(video.getThumbnail())
                .channel(lecture.getChannel())
                .isPlaylist(false)
                .viewDuration(viewDuration)
                .lectureDuration(video.getDuration())
                .progress(progress)
                .content(review.getContent())
                .rating(review.getSubmittedRating())
                .submittedAt((new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(review.getSubmittedDate())))
                .isReviewAllowed(isReviewAllowed(progress, lecture.getReviewed()))
                .build();

    }

    String applyReview(Lecture lecture, ReviewSubmitRequest reviewSubmitRequest) {

        String lectureCode = "";

        if (lecture.getPlaylist()) {
            Playlist playlist = playlistRepository.getById(lecture.getSourceId());
            lectureCode = playlist.getPlaylistCode();
            applyReviewToPlaylist(reviewSubmitRequest, playlist);
        }
        else {
            Video video = videoRepository.getById(lecture.getSourceId());
            lectureCode = video.getVideoCode();
            applyReviewToVideo(reviewSubmitRequest, video);
        }

        lecture.setReviewed(true);

        lectureRepository.updateById(lecture.getId(), lecture);

        return lectureCode;
    }

    void applyReviewToPlaylist(ReviewSubmitRequest reviewSubmitRequest, Playlist playlist) {

        playlist.setReviewCount(playlist.getReviewCount() + 1);
        playlist.setAccumulatedRating(playlist.getAccumulatedRating() + reviewSubmitRequest.getSubmittedRating());

        playlistRepository.updateById(playlist.getPlaylistId(), playlist);
    }

    void applyReviewToVideo(ReviewSubmitRequest reviewSubmitRequest, Video video) {

        video.setReviewCount(video.getReviewCount() + 1);
        video.setAccumulatedRating(video.getAccumulatedRating() + reviewSubmitRequest.getSubmittedRating());

        videoRepository.updateById(video.getVideoId(), video);
    }

    Review getReview(Lecture lecture) {
        Review review = Review.builder().build();

        review.setSubmittedRating(null);
        review.setContent(null);
        review.setSubmittedDate(new Timestamp(0));

        if(lecture.getReviewed())
            review = reviewRepository.getByLectureId(lecture.getId());

        return review;
    }

    boolean isReviewAllowed(int progress, boolean isReviewed) {
        return progress >= 70 && !isReviewed;
    }

    LectureBrief4Review getVideoCountData(Long lectureId) {
        List<CourseVideo> courseVideoList = courseVideoRepository.getListByLectureId(lectureId);
        int completedVideoCount = (int) courseVideoList.stream()
                .filter(CourseVideo::isComplete)
                .count();

        return LectureBrief4Review.builder()
                .totalVideoCount(courseVideoList.size())
                .completedVideoCount(completedVideoCount)
                .build();
    }
}
