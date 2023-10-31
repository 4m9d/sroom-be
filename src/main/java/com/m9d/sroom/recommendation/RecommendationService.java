package com.m9d.sroom.recommendation;

import com.m9d.sroom.common.entity.PlaylistEntity;
import com.m9d.sroom.common.entity.VideoEntity;
import com.m9d.sroom.common.repository.playlist.PlaylistRepository;
import com.m9d.sroom.common.repository.video.VideoRepository;
import com.m9d.sroom.lecture.LectureService;
import com.m9d.sroom.playlist.PlaylistService;
import com.m9d.sroom.recommendation.dto.RecommendLecture;
import com.m9d.sroom.recommendation.dto.Recommendations;
import com.m9d.sroom.video.VideoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class RecommendationService {

    private final PlaylistRepository playlistRepository;
    private final VideoRepository videoRepository;
    private final LectureService lectureService;
    private final VideoService videoService;
    private final PlaylistService playlistService;

    public RecommendationService(PlaylistRepository playlistRepository, VideoRepository videoRepository,
                                 LectureService lectureService, VideoService videoService, PlaylistService playlistService) {
        this.lectureService = lectureService;
        this.playlistRepository = playlistRepository;
        this.videoRepository = videoRepository;
        this.videoService = videoService;
        this.playlistService = playlistService;
    }

    @Transactional
    public Recommendations getRecommendations(Long memberId) {
        List<RecommendLecture> generalRecommendLectureList = new ArrayList<>();
        List<RecommendLecture> channelRecommendLectureList = getRecommendsByChannel(memberId);

        generalRecommendLectureList.addAll(getRecommendLectures(videoService.getTopRatedVideos(20)));
        generalRecommendLectureList.addAll(getRecommendLectures(playlistService.getTopRatedPlaylists(20)));

        Set<String> enrolledLectureSet = lectureService.getEnrolledLectures(memberId);

        for (String lectureCode : enrolledLectureSet) {
            generalRecommendLectureList.removeIf(recommendLecture -> (recommendLecture.getLectureCode().equals(lectureCode)));
            channelRecommendLectureList.removeIf(recommendLecture -> (recommendLecture.getLectureCode().equals(lectureCode)));
        }

        Collections.shuffle(generalRecommendLectureList);
        Collections.shuffle(channelRecommendLectureList);
        Recommendations recommendations = Recommendations.builder()
                .generalRecommendations(generalRecommendLectureList.stream()
                        .limit(20)
                        .collect(Collectors.toList()))
                .channelRecommendations(channelRecommendLectureList.stream()
                        .limit(20)
                        .collect(Collectors.toList()))
                .build();

        return recommendations;
    }


    public List<RecommendLecture> getRecommendsByChannel(Long memberId) {
        HashSet<RecommendLecture> recommendLectureSetByChannel = new HashSet<>();
        List<String> channels = lectureService.getMostEnrolledChannels(memberId);

        final int SELECT_BY_RANDOM_LIMIT = 2;
        final int SELECT_BY_PUBLISH_DATE_LIMIT = 3;
        final int SELECT_BY_VIEWED_LIMIT = 5;

        for (String channelName : channels) {
            List<Object> lectures = new ArrayList<>();

            lectures.addAll(videoRepository.getRandomByChannel(channelName, SELECT_BY_RANDOM_LIMIT));
            lectures.addAll(playlistRepository.getRandomByChannel(channelName, SELECT_BY_RANDOM_LIMIT));

            lectures.addAll(videoRepository.getViewCountOrderByChannel(channelName, SELECT_BY_VIEWED_LIMIT));
            lectures.addAll(playlistRepository.getViewCountOrderByChannel(channelName, SELECT_BY_VIEWED_LIMIT));

            lectures.addAll(videoRepository.getLatestOrderByChannel(channelName, SELECT_BY_PUBLISH_DATE_LIMIT));
            lectures.addAll(playlistRepository.getLatestOrderByChannel(channelName, SELECT_BY_PUBLISH_DATE_LIMIT));

            recommendLectureSetByChannel.addAll(getRecommendLectures(lectures));
        }

        return new ArrayList<>(recommendLectureSetByChannel);
    }

    public List<RecommendLecture> getRecommendLectures(List<?> lectures) {
        List<RecommendLecture> recommendLectures = new ArrayList<>();

        for (Object lecture : lectures) {
            if (lecture instanceof VideoEntity) {
                VideoEntity video = (VideoEntity) lecture;
                recommendLectures.add(RecommendLecture.builder()
                        .lectureTitle(video.getTitle())
                        .description(video.getDescription())
                        .channel(video.getChannel())
                        .lectureCode(video.getVideoCode())
                        .isPlaylist(false)
                        .rating((double) video.getAccumulatedRating() / video.getReviewCount())
                        .reviewCount(video.getReviewCount())
                        .thumbnail(video.getThumbnail())
                        .build());
            } else if (lecture instanceof PlaylistEntity) {
                PlaylistEntity playlist = (PlaylistEntity) lecture;
                recommendLectures.add(RecommendLecture.builder()
                        .lectureTitle(playlist.getTitle())
                        .description(playlist.getDescription())
                        .channel(playlist.getChannel())
                        .lectureCode(playlist.getPlaylistCode())
                        .isPlaylist(true)
                        .rating((double) playlist.getAccumulatedRating() / playlist.getReviewCount())
                        .reviewCount(playlist.getReviewCount())
                        .thumbnail(playlist.getThumbnail())
                        .build());
            }
        }
        return recommendLectures;
    }
}
