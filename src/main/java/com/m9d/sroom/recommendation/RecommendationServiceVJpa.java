package com.m9d.sroom.recommendation;

import com.m9d.sroom.common.entity.jpa.*;
import com.m9d.sroom.common.repository.member.MemberJpaRepository;
import com.m9d.sroom.common.repository.playlist.PlaylistJpaRepository;
import com.m9d.sroom.common.repository.recommend.RecommendJpaRepository;
import com.m9d.sroom.common.repository.video.VideoJpaRepository;
import com.m9d.sroom.recommendation.dto.RecommendLecture;
import com.m9d.sroom.recommendation.dto.Recommendations;
import groovy.util.logging.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.Collectors;

import static com.m9d.sroom.recommendation.constant.RecommendationConstant.TOP_RATED_LECTURES_COUNT;

@Service
@Slf4j
public class RecommendationServiceVJpa {

    private final DomainRecommendation domainRecommendation;

    private final MemberJpaRepository memberRepository;

    private final VideoJpaRepository videoRepository;

    private final PlaylistJpaRepository playlistRepository;

    private final RecommendJpaRepository recommendRepository;

    public RecommendationServiceVJpa(DomainRecommendation domainRecommendation, MemberJpaRepository memberRepository,
                                     VideoJpaRepository videoRepository, PlaylistJpaRepository playlistRepository,
                                     RecommendJpaRepository recommendRepository) {
        this.domainRecommendation = domainRecommendation;
        this.memberRepository = memberRepository;
        this.videoRepository = videoRepository;
        this.playlistRepository = playlistRepository;
        this.recommendRepository = recommendRepository;
    }

    @Transactional
    public Recommendations getRecommendations(Long memberId) {
        MemberEntity member = memberRepository.getById(memberId);

        List<RecommendLecture> generalRecommendLectureList = getGeneralRecommends();
        List<RecommendLecture> channelRecommendLectureList = getRecommendsByChannel(member);
        List<RecommendLecture> societyRecommendLectureList = new ArrayList<>();
        List<RecommendLecture> scienceRecommendLectureList = new ArrayList<>();
        List<RecommendLecture> economicRecommendLectureList = new ArrayList<>();
        List<RecommendLecture> techRecommendLectureList = new ArrayList<>();

        societyRecommendLectureList.addAll(domainRecommendation.getSocietyRecommendations());
        scienceRecommendLectureList.addAll(domainRecommendation.getScienceRecommendations());
        economicRecommendLectureList.addAll(domainRecommendation.getEconomicRecommendations());
        techRecommendLectureList.addAll(domainRecommendation.getTechRecommendations());

        List<LectureEntity> enrolledLectures = member.getLectures();

        for (LectureEntity lecture : enrolledLectures) {
            List<String> lectureCodes = new ArrayList<>();

            if(lecture.getIsPlaylist()) {
                PlaylistEntity playlist = playlistRepository.getById(lecture.getSourceId());
                lectureCodes.add(playlist.getPlaylistCode());
                lectureCodes.addAll(playlist.getVideoList().stream()
                                .map(VideoEntity::getVideoCode)
                        .collect(Collectors.toList()));
            }
            else {
                lectureCodes.add(videoRepository.getById(lecture.getSourceId()).getVideoCode());
            }

            for (String lectureCode: lectureCodes) {
                generalRecommendLectureList.removeIf(recommendLecture -> (recommendLecture.getLectureCode().equals(lectureCode)));
                channelRecommendLectureList.removeIf(recommendLecture -> (recommendLecture.getLectureCode().equals(lectureCode)));
                societyRecommendLectureList.removeIf(recommendLecture -> (recommendLecture.getLectureCode().equals(lectureCode)));
                scienceRecommendLectureList.removeIf(recommendLecture -> (recommendLecture.getLectureCode().equals(lectureCode)));
                economicRecommendLectureList.removeIf(recommendLecture -> (recommendLecture.getLectureCode().equals(lectureCode)));
                techRecommendLectureList.removeIf(recommendLecture -> (recommendLecture.getLectureCode().equals(lectureCode)));
            }
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

        generalRecommendLectureList.addAll(getRecommendLectures(videoRepository.getTopRatedOrder(TOP_RATED_LECTURES_COUNT)));
        generalRecommendLectureList.addAll(getRecommendLectures(playlistRepository.getTopRatedOrder(TOP_RATED_LECTURES_COUNT)));

        return generalRecommendLectureList;
    }


    public List<RecommendLecture> getRecommendsByChannel(MemberEntity member) {
        HashSet<RecommendLecture> recommendLectureSetByChannel = new HashSet<>();
        List<String> channels = member.getChannelListOrderByCount();

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
                        .lectureTitle(video.getContentInfo().getTitle())
                        .description(video.getContentInfo().getDescription())
                        .channel(video.getContentInfo().getChannel())
                        .lectureCode(video.getVideoCode())
                        .isPlaylist(false)
                        .rating(Double.parseDouble(decimalFormat.format((double) video.getReview().getAccumulatedRating()
                                / video.getReview().getReviewCount())))
                        .reviewCount(video.getReview().getReviewCount())
                        .thumbnail(video.getContentInfo().getThumbnail())
                        .build());
            } else if (lecture instanceof PlaylistEntity) {
                PlaylistEntity playlist = (PlaylistEntity) lecture;
                recommendLectures.add(RecommendLecture.builder()
                        .lectureTitle(playlist.getContentInfo().getTitle())
                        .description(playlist.getContentInfo().getDescription())
                        .channel(playlist.getContentInfo().getChannel())
                        .lectureCode(playlist.getPlaylistCode())
                        .isPlaylist(true)
                        .rating(Double.parseDouble(decimalFormat.format((double) playlist.getReview().getAccumulatedRating()
                                / playlist.getReview().getReviewCount())))
                        .reviewCount(playlist.getReview().getReviewCount())
                        .thumbnail(playlist.getContentInfo().getThumbnail())
                        .build());
            }
        }
        return recommendLectures;
    }

}
