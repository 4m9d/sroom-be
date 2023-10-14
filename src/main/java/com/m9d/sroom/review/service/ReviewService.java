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

        List<LectureDto> lectureDtoList = lectureRepository.getListByCourseId(courseId);
        List<LectureBrief4Review> lectures = new ArrayList<>();

        for(LectureDto lectureDto : lectureDtoList) {
            if(lectureDto.getPlaylist()) {
                lectures.add(getPlaylistLectureBrief4Review(lectureDto));
            }
            else {
                lectures.add(getVideoLectureBrief4Review(lectureDto));
            }
        }

        return LectureBriefList4Review.builder()
                .lectures(lectures)
                .build();
    }

    @Transactional
    public ReviewSubmitResponse submitReview(Long memberId, Long lectureId, ReviewSubmitRequest reviewSubmitRequest) {

        LectureDto lectureDto = lectureRepository.getById(lectureId);
        String lectureCode = "";

        lectureCode = applyReview(lectureDto, reviewSubmitRequest);

        ReviewDto reviewDto = ReviewDto.builder()
                .lectureId(lectureId)
                .sourceCode(lectureCode)
                .memberId(memberId)
                .content(reviewSubmitRequest.getReviewContent())
                .submittedRating(reviewSubmitRequest.getSubmittedRating())
                .build();

        Long reviewId = reviewRepository.save(reviewDto).getReviewId();

        return ReviewSubmitResponse.builder()
                .reviewId(reviewId)
                .lectureId(lectureId)
                .submittedRating(reviewSubmitRequest.getSubmittedRating())
                .reviewContent(reviewSubmitRequest.getReviewContent())
                .build();
    }

    LectureBrief4Review getPlaylistLectureBrief4Review(LectureDto lectureDto) {

        LectureBrief4Review videoCountData = getVideoCountData(lectureDto.getId());
        PlaylistDto playlistDto = playlistRepository.getById(lectureDto.getSourceId());
        int progress = (videoCountData.getCompletedVideoCount() * 100) / videoCountData.getTotalVideoCount();

        ReviewDto reviewDto = getReview(lectureDto);
        String submittedDate;

        if (reviewDto.getSubmittedDate() != null) {
            submittedDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(reviewDto.getSubmittedDate());
        }
        else {
            submittedDate = null;
        }

        return LectureBrief4Review.builder()
                .index(lectureDto.getLectureIndex())
                .lectureId(lectureDto.getId())
                .title(playlistDto.getTitle())
                .thumbnail(playlistDto.getThumbnail())
                .channel(lectureDto.getChannel())
                .isPlaylist(true)
                .totalVideoCount(videoCountData.getTotalVideoCount())
                .completedVideoCount(videoCountData.getCompletedVideoCount())
                .progress(progress)
                .content(reviewDto.getContent())
                .rating(reviewDto.getSubmittedRating())
                .submittedAt(submittedDate)
                .isReviewAllowed(isReviewAllowed(progress, lectureDto.getReviewed()))
                .build();
    }

    LectureBrief4Review getVideoLectureBrief4Review(LectureDto lectureDto) {

        CourseVideoDto courseVideoDto = courseVideoRepository.getListByLectureId(lectureDto.getId())
                .get(0);
        VideoDto videoDto = videoRepository.getById(lectureDto.getSourceId());
        int progress = (courseVideoDto.getMaxDuration() * 100) / videoDto.getDuration();

        if(progress < 70 && courseVideoDto.isComplete())
            progress = 100;

        ReviewDto reviewDto = getReview(lectureDto);
        String submittedDate;

        if (reviewDto.getSubmittedDate() != null) {
            submittedDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(reviewDto.getSubmittedDate());
        }
        else {
            submittedDate = null;
        }

        return LectureBrief4Review.builder()
                .index(lectureDto.getLectureIndex())
                .lectureId(lectureDto.getId())
                .title(videoDto.getTitle())
                .thumbnail(videoDto.getThumbnail())
                .channel(lectureDto.getChannel())
                .isPlaylist(false)
                .viewDuration(courseVideoDto.getMaxDuration())
                .lectureDuration(videoDto.getDuration())
                .progress(progress)
                .content(reviewDto.getContent())
                .rating(reviewDto.getSubmittedRating())
                .submittedAt(submittedDate)
                .isReviewAllowed(isReviewAllowed(progress, lectureDto.getReviewed()))
                .build();

    }

    String applyReview(LectureDto lectureDto, ReviewSubmitRequest reviewSubmitRequest) {

        String lectureCode = "";

        if (lectureDto.getPlaylist()) {
            PlaylistDto playlistDto = playlistRepository.getById(lectureDto.getSourceId());
            lectureCode = playlistDto.getPlaylistCode();
            applyReviewToPlaylist(reviewSubmitRequest, playlistDto);
        }
        else {
            VideoDto videoDto = videoRepository.getById(lectureDto.getSourceId());
            lectureCode = videoDto.getVideoCode();
            applyReviewToVideo(reviewSubmitRequest, videoDto);
        }

        lectureDto.setReviewed(true);

        lectureRepository.updateById(lectureDto.getId(), lectureDto);

        return lectureCode;
    }

    void applyReviewToPlaylist(ReviewSubmitRequest reviewSubmitRequest, PlaylistDto playlistDto) {

        playlistDto.setReviewCount(playlistDto.getReviewCount() + 1);
        playlistDto.setAccumulatedRating(playlistDto.getAccumulatedRating() + reviewSubmitRequest.getSubmittedRating());

        playlistRepository.updateById(playlistDto.getPlaylistId(), playlistDto);
    }

    void applyReviewToVideo(ReviewSubmitRequest reviewSubmitRequest, VideoDto videoDto) {

        videoDto.setReviewCount(videoDto.getReviewCount() + 1);
        videoDto.setAccumulatedRating(videoDto.getAccumulatedRating() + reviewSubmitRequest.getSubmittedRating());

        videoRepository.updateById(videoDto.getVideoId(), videoDto);
    }

    ReviewDto getReview(LectureDto lectureDto) {
        ReviewDto reviewDto = ReviewDto.builder().build();

        reviewDto.setSubmittedRating(null);
        reviewDto.setContent(null);
        reviewDto.setSubmittedDate(null);

        if(lectureDto.getReviewed())
            reviewDto = reviewRepository.getByLectureId(lectureDto.getId());

        return reviewDto;
    }

    boolean isReviewAllowed(int progress, boolean isReviewed) {
        return progress >= 70 && !isReviewed;
    }

    LectureBrief4Review getVideoCountData(Long lectureId) {
        List<CourseVideoDto> courseVideoDtoList = courseVideoRepository.getListByLectureId(lectureId);
        int completedVideoCount = (int) courseVideoDtoList.stream()
                .filter(CourseVideoDto::isComplete)
                .count();

        return LectureBrief4Review.builder()
                .totalVideoCount(courseVideoDtoList.size())
                .completedVideoCount(completedVideoCount)
                .build();
    }
}
