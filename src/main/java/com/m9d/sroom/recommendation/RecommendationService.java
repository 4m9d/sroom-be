package com.m9d.sroom.recommendation;

import com.m9d.sroom.common.entity.PlaylistEntity;
import com.m9d.sroom.common.entity.RecommendEntity;
import com.m9d.sroom.common.entity.VideoEntity;
import com.m9d.sroom.common.repository.playlist.PlaylistRepository;
import com.m9d.sroom.common.repository.recommend.RecommendRepository;
import com.m9d.sroom.common.repository.video.VideoRepository;
import com.m9d.sroom.lecture.LectureService;
import com.m9d.sroom.playlist.PlaylistService;
import com.m9d.sroom.recommendation.dto.RecommendLecture;
import com.m9d.sroom.recommendation.dto.Recommendations;
import com.m9d.sroom.video.VideoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.Collectors;

import static com.m9d.sroom.recommendation.constant.RecommendationConstant.TOP_RATED_LECTURES_COUNT;

@Slf4j
@Service
public class RecommendationService {

    private final DomainRecommendation domainRecommendation;
    private final PlaylistRepository playlistRepository;
    private final RecommendRepository recommendRepository;
    private final VideoRepository videoRepository;
    private final LectureService lectureService;
    private final VideoService videoService;
    private final PlaylistService playlistService;

    public RecommendationService(DomainRecommendation domainRecommendation,
                                 PlaylistRepository playlistRepository, RecommendRepository recommendRepository,
                                 VideoRepository videoRepository, LectureService lectureService, VideoService videoService,
                                 PlaylistService playlistService) {
        this.domainRecommendation = domainRecommendation;
        this.lectureService = lectureService;
        this.recommendRepository = recommendRepository;
        this.playlistRepository = playlistRepository;
        this.videoRepository = videoRepository;
        this.videoService = videoService;
        this.playlistService = playlistService;
    }

    @Transactional
    public Recommendations getRecommendations(Long memberId) {
        List<RecommendLecture> generalRecommendLectureList = getGeneralRecommends();
        List<RecommendLecture> channelRecommendLectureList = getRecommendsByChannel(memberId);
        List<RecommendLecture> societyRecommendLectureList = new ArrayList<>();
        List<RecommendLecture> scienceRecommendLectureList = new ArrayList<>();
        List<RecommendLecture> economicRecommendLectureList = new ArrayList<>();
        List<RecommendLecture> techRecommendLectureList = new ArrayList<>();

        societyRecommendLectureList.addAll(domainRecommendation.getSocietyRecommendations());
        scienceRecommendLectureList.addAll(domainRecommendation.getScienceRecommendations());
        economicRecommendLectureList.addAll(domainRecommendation.getEconomicRecommendations());
        techRecommendLectureList.addAll(domainRecommendation.getTechRecommendations());

        Set<String> enrolledLectureSet = lectureService.getEnrolledLectures(memberId);

        for (String lectureCode : enrolledLectureSet) {
            generalRecommendLectureList.removeIf(recommendLecture -> (recommendLecture.getLectureCode().equals(lectureCode)));
            channelRecommendLectureList.removeIf(recommendLecture -> (recommendLecture.getLectureCode().equals(lectureCode)));
            societyRecommendLectureList.removeIf(recommendLecture -> (recommendLecture.getLectureCode().equals(lectureCode)));
            scienceRecommendLectureList.removeIf(recommendLecture -> (recommendLecture.getLectureCode().equals(lectureCode)));
            economicRecommendLectureList.removeIf(recommendLecture -> (recommendLecture.getLectureCode().equals(lectureCode)));
            techRecommendLectureList.removeIf(recommendLecture -> (recommendLecture.getLectureCode().equals(lectureCode)));
        }

        Collections.shuffle(generalRecommendLectureList);
        Collections.shuffle(channelRecommendLectureList);
        Collections.shuffle(societyRecommendLectureList);
        Collections.shuffle(scienceRecommendLectureList);
        Collections.shuffle(economicRecommendLectureList);
        Collections.shuffle(techRecommendLectureList);

        Recommendations recommendations = Recommendations.builder()
                .generalRecommendations(generalRecommendLectureList.stream()
                        .limit(10)
                        .collect(Collectors.toList()))
                .channelRecommendations(channelRecommendLectureList.stream()
                        .limit(10)
                        .collect(Collectors.toList()))
                .societyRecommendations(societyRecommendLectureList.stream()
                        .limit(10)
                        .collect(Collectors.toList()))
                .scienceRecommendations(scienceRecommendLectureList.stream()
                        .limit(10)
                        .collect(Collectors.toList()))
                .economicRecommendations(economicRecommendLectureList.stream()
                        .limit(10)
                        .collect(Collectors.toList()))
                .techRecommendations(techRecommendLectureList.stream()
                        .limit(10)
                        .collect(Collectors.toList()))
                .build();

        return recommendations;
    }

    public List<RecommendLecture> getGeneralRecommends() {
        List<RecommendLecture> generalRecommendLectureList = new ArrayList<>();

        generalRecommendLectureList.addAll(getRecommendLectures(videoService.getTopRatedVideos(TOP_RATED_LECTURES_COUNT)));
        generalRecommendLectureList.addAll(getRecommendLectures(playlistService.getTopRatedPlaylists(TOP_RATED_LECTURES_COUNT)));

        return generalRecommendLectureList;
    }


    public List<RecommendLecture> getRecommendsByChannel(Long memberId) {
        HashSet<RecommendLecture> recommendLectureSetByChannel = new HashSet<>();
        List<String> channels = lectureService.getMostEnrolledChannels(memberId);

        final int SELECT_BY_RANDOM_LIMIT = 1;
        final int SELECT_BY_PUBLISH_DATE_LIMIT = 2;
        final int SELECT_BY_VIEWED_LIMIT = 3;

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

    public List<RecommendLecture> getRecommendsByDomain(int domainId) {
        List<RecommendEntity> recommendEntities = recommendRepository.getListByDomain(domainId);
        List<Object> lectures = new ArrayList<>();

        for (RecommendEntity recommendEntity : recommendEntities) {
            if (recommendEntity.getIsPlaylist()) {
                lectures.add(playlistRepository.getByCode(recommendEntity.getSourceCode()));
            }
            else {
                lectures.add(videoRepository.getByCode(recommendEntity.getSourceCode()));
            }
        }
        return getRecommendLectures(lectures);
    }

    private List<RecommendLecture> getRecommendLectures(List<?> lectures) {
        List<RecommendLecture> recommendLectures = new ArrayList<>();
        DecimalFormat decimalFormat = new DecimalFormat("#.#");

        for (Object lecture : lectures) {
            if (lecture instanceof VideoEntity) {
                VideoEntity video = (VideoEntity) lecture;
                recommendLectures.add(RecommendLecture.builder()
                        .lectureTitle(video.getTitle())
                        .description(video.getDescription())
                        .channel(video.getChannel())
                        .lectureCode(video.getVideoCode())
                        .isPlaylist(false)
                        .rating(Double.parseDouble(decimalFormat.format((double) video.getAccumulatedRating()
                                / video.getReviewCount())))
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
                        .rating(Double.parseDouble(decimalFormat.format((double) playlist.getAccumulatedRating()
                                / playlist.getReviewCount())))
                        .reviewCount(playlist.getReviewCount())
                        .thumbnail(playlist.getThumbnail())
                        .build());
            }
        }
        return recommendLectures;
    }
}
