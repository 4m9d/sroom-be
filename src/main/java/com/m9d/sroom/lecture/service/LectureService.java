package com.m9d.sroom.lecture.service;

import com.m9d.sroom.global.model.Playlist;
import com.m9d.sroom.global.model.Video;
import com.m9d.sroom.course.repository.CourseRepository;
import com.m9d.sroom.lecture.dto.request.KeywordSearchParam;
import com.m9d.sroom.lecture.dto.request.LectureDetailParam;
import com.m9d.sroom.lecture.dto.response.Index;
import com.m9d.sroom.lecture.dto.response.Lecture;
import com.m9d.sroom.lecture.dto.response.ReviewBrief;
import com.m9d.sroom.lecture.dto.response.*;
import com.m9d.sroom.lecture.exception.TwoOnlyParamTrueException;
import com.m9d.sroom.lecture.exception.VideoIndexParamException;
import com.m9d.sroom.lecture.repository.LectureRepository;
import com.m9d.sroom.util.DateUtil;
import com.m9d.sroom.util.youtube.YoutubeApi;
import com.m9d.sroom.util.youtube.YoutubeUtil;
import com.m9d.sroom.util.youtube.resource.LectureListReq;
import com.m9d.sroom.util.youtube.resource.PlaylistReq;
import com.m9d.sroom.util.youtube.resource.PlaylistItemReq;
import com.m9d.sroom.util.youtube.resource.VideoReq;
import com.m9d.sroom.util.youtube.vo.playlist.PlaylistVo;
import com.m9d.sroom.util.youtube.vo.playlistitem.PlaylistVideoItemVo;
import com.m9d.sroom.util.youtube.vo.playlistitem.PlaylistVideoVo;
import com.m9d.sroom.util.youtube.vo.search.SearchItemVo;
import com.m9d.sroom.util.youtube.vo.search.SearchSnippetVo;
import com.m9d.sroom.util.youtube.vo.search.SearchVo;
import com.m9d.sroom.util.youtube.vo.video.VideoVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.HtmlUtils;
import reactor.core.publisher.Mono;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static com.m9d.sroom.course.constant.CourseConstant.PLAYLIST_UPDATE_THRESHOLD_HOURS;
import static com.m9d.sroom.course.constant.CourseConstant.VIDEO_UPDATE_THRESHOLD_HOURS;
import static com.m9d.sroom.lecture.constant.LectureConstant.*;
import static com.m9d.sroom.util.youtube.YoutubeUtil.*;

@Service
@Slf4j
public class LectureService {

    private final LectureRepository lectureRepository;
    private final CourseRepository courseRepository;
    private final YoutubeUtil youtubeUtil;
    private final YoutubeApi youtubeApi;
    private final DateUtil dateUtil;

    public LectureService(LectureRepository lectureRepository, CourseRepository courseRepository, YoutubeUtil youtubeUtil, YoutubeApi youtubeApi, DateUtil dateUtil) {
        this.lectureRepository = lectureRepository;
        this.courseRepository = courseRepository;
        this.youtubeUtil = youtubeUtil;
        this.youtubeApi = youtubeApi;
        this.dateUtil = dateUtil;
    }

    @Transactional
    public KeywordSearch searchByKeyword(Long memberId, KeywordSearchParam keywordSearchParam) {
        log.info("lecture keyword search. keyword : {}", keywordSearchParam.getKeyword());
        Mono<SearchVo> searchVoMono = getSearchVoMono(keywordSearchParam);
        Set<String> enrolledLectureSet = getEnrolledLectures(memberId);

        SearchVo searchVo = blockFromMono(searchVoMono);
        String nextPageToken = Optional.of(searchVo)
                .map(SearchVo::getNextPageToken)
                .orElse(null);
        List<Lecture> lectureList = getSearchedLectureList(searchVo, enrolledLectureSet);

        return KeywordSearch.builder()
                .nextPageToken(nextPageToken)
                .resultPerPage(searchVo.getPageInfo().getResultsPerPage())
                .lectures(lectureList)
                .build();
    }

    private Mono<SearchVo> getSearchVoMono(KeywordSearchParam keywordSearchParam) {
        String encodedKeyword = URLEncoder.encode(keywordSearchParam.getKeyword(), StandardCharsets.UTF_8);

        return youtubeApi.getSearchVo(LectureListReq.builder()
                .keyword(encodedKeyword)
                .filter(keywordSearchParam.getFilter())
                .limit(keywordSearchParam.getLimit())
                .pageToken(keywordSearchParam.getNext_page_token())
                .build());
    }

    public List<Lecture> getSearchedLectureList(SearchVo searchVo, Set<String> enrolledLectureSet) {
        List<CompletableFuture<Lecture>> futures = searchVo.getItems().stream()
                .map(item -> CompletableFuture.supplyAsync(() -> getLecture(enrolledLectureSet, item)))
                .collect(Collectors.toList());
        List<Lecture> lectures = futures.stream()
                .map(CompletableFuture::join)
                .collect(Collectors.toList());
        return lectures;
    }

    private Lecture getLecture(Set<String> enrolledLectureSet, SearchItemVo item) {
        SearchSnippetVo snippetVo = item.getSnippet();
        String thumbnail = youtubeUtil.selectThumbnailInVo(snippetVo.getThumbnails());

        String lectureCode;
        long viewCount = 0L;
        int videoCount = 1;
        String description;

        boolean isPlaylist = item.getId().getKind().equals(JSONNODE_TYPE_PLAYLIST);
        if (isPlaylist) {
            lectureCode = item.getId().getPlaylistId();
            Playlist playlist = getSearchedPlaylistLast(lectureCode);
            videoCount = playlist.getLectureCount();
            description = playlist.getDescription();
        } else {
            lectureCode = item.getId().getVideoId();
            Video video = getSearchedVideoLast(lectureCode);
            viewCount = video.getViewCount();
            description = video.getDescription();
        }

        return Lecture.builder()
                .lectureTitle(unescapeHtml(snippetVo.getTitle()))
                .description(unescapeHtml(description))
                .channel(unescapeHtml(snippetVo.getChannelTitle()))
                .lectureCode(lectureCode)
                .enrolled(enrolledLectureSet.contains(lectureCode))
                .publishedAt(dateUtil.convertISOToString(snippetVo.getPublishTime()))
                .playlist(isPlaylist)
                .lectureCount(videoCount)
                .viewCount(viewCount)
                .thumbnail(thumbnail)
                .build();
    }

    private Playlist getSearchedPlaylistLast(String lectureCode) {
        Optional<Playlist> playlistOptional = lectureRepository.findVideoCountAndDescription(lectureCode);

        Playlist playlist;
        if (playlistOptional.isPresent() && dateUtil.validateExpiration(playlistOptional.get().getUpdatedAt(), PLAYLIST_UPDATE_THRESHOLD_HOURS)) {
            playlist = playlistOptional.get();
        } else {
            Mono<PlaylistVo> playlistVoMono = youtubeApi.getPlaylistVo(PlaylistReq.builder()
                    .playlistCode(lectureCode)
                    .build());

            PlaylistVo playlistVo = youtubeUtil.safeGetVo(playlistVoMono);
            playlist = Playlist.builder()
                    .playlistCode(lectureCode)
                    .description(playlistVo.getItems().get(FIRST_INDEX).getSnippet().getDescription())
                    .lectureCount(playlistVo.getItems().get(FIRST_INDEX).getContentDetails().getItemCount())
                    .build();
        }

        return playlist;
    }

    @Transactional
    public ResponseEntity<?> getLectureDetail(Long memberId, boolean isPlaylist, String lectureCode, LectureDetailParam lectureDetailParam) {
        lectureDetailParamValidate(isPlaylist, lectureDetailParam);
        log.info("lecture detail request. memberId = {}, lectureCode = {}", memberId, lectureCode);

        Object response;
        if (lectureDetailParam.isIndex_only()) {
            response = getPlaylistItemList(lectureCode, lectureDetailParam.getIndex_next_token(), lectureDetailParam.getIndex_limit());
        } else if (lectureDetailParam.isReview_only()) {
            response = getReviewBriefList(lectureCode, lectureDetailParam.getReview_offset(), lectureDetailParam.getReview_limit());
        } else if (isPlaylist) {
            response = getPlaylistDetail(memberId, lectureCode, lectureDetailParam.getIndex_next_token(), lectureDetailParam.getReview_limit());
        } else {
            response = getVideoDetail(memberId, lectureCode, lectureDetailParam.getReview_limit());
        }

        return ResponseEntity.ok(response);
    }

    public void lectureDetailParamValidate(boolean isPlaylist, LectureDetailParam lectureDetailParam) {
        if (lectureDetailParam.isIndex_only() && lectureDetailParam.isReview_only()) {
            throw new TwoOnlyParamTrueException();
        }
        if (!isPlaylist && lectureDetailParam.isIndex_only()) {
            throw new VideoIndexParamException();
        }
    }

    private IndexInfo getPlaylistItemList(String playlistCode, String nextPageToken, int indexLimit) {
        Mono<PlaylistVideoVo> playlistVideoVoMono = youtubeApi.getPlaylistVideoVo(PlaylistItemReq.builder()
                .playlistCode(playlistCode)
                .nextPageToken(nextPageToken)
                .limit(indexLimit)
                .build());

        return getIndexInfo(playlistVideoVoMono);
    }

    private List<ReviewBrief> getReviewBriefList(String lectureCode, int reviewOffset, int reviewLimit) {
        return lectureRepository.getReviewBriefList(lectureCode, reviewOffset, reviewLimit);
    }

    public PlaylistDetail getPlaylistDetail(Long memberId, String playlistCode, String nextPageToken, int reviewLimit) {
        Mono<PlaylistVo> playlistVoMono = youtubeApi.getPlaylistVo(PlaylistReq.builder()
                .playlistCode(playlistCode)
                .build());
        Mono<PlaylistVideoVo> playlistVideoVoMono = youtubeApi.getPlaylistVideoVo(PlaylistItemReq.builder()
                .playlistCode(playlistCode)
                .nextPageToken(nextPageToken)
                .limit(DEFAULT_INDEX_COUNT)
                .build());

        Set<String> enrolledPlaylistSet = lectureRepository.getPlaylistByMemberId(memberId);
        Playlist playlist = youtubeUtil.getPlaylistFromMono(playlistVoMono);
        List<CourseBrief> courseBriefList = courseRepository.getCourseBriefListByMember(memberId);
        IndexInfo indexInfo = getIndexInfo(playlistVideoVoMono);
        List<ReviewBrief> reviewList = lectureRepository.getReviewBriefList(playlistCode, DEFAULT_REVIEW_OFFSET, reviewLimit);

        return PlaylistDetail.builder()
                .lectureCode(playlistCode)
                .lectureTitle(playlist.getTitle())
                .channel(playlist.getChannel())
                .description(playlist.getDescription())
                .playlist(true)
                .publishedAt(playlist.getPublishedAt().toLocalDateTime().toLocalDate().format(DateTimeFormatter.ISO_LOCAL_DATE))
                .enrolled(enrolledPlaylistSet.contains(playlistCode))
                .lectureCount(playlist.getLectureCount())
                .thumbnail(playlist.getThumbnail())
                .indexes(indexInfo)
                .duration(indexInfo.getTotalDuration())
                .reviews(reviewList)
                .reviewCount(reviewList.size())
                .rating(calculateAverageRating(reviewList))
                .courses(courseBriefList)
                .build();
    }

    private IndexInfo getIndexInfo(Mono<PlaylistVideoVo> playlistVideoVoMono) {
        AtomicInteger totalDurationSeconds = new AtomicInteger(0);
        List<CompletableFuture<Index>> futureList = new ArrayList<>();
        PlaylistVideoVo playlistVideoVo = blockFromMono(playlistVideoVoMono);

        List<Index> indexList = getIndexList(futureList, playlistVideoVo);

        for (Index index : indexList) {
            totalDurationSeconds.addAndGet(index.getDuration());
        }

        String nextPageToken = Optional.of(playlistVideoVo)
                .map(PlaylistVideoVo::getNextPageToken)
                .orElse(null);

        return IndexInfo.builder()
                .indexList(indexList)
                .totalDuration(totalDurationSeconds.get())
                .nextPageToken(nextPageToken)
                .build();
    }

    private List<Index> getIndexList(List<CompletableFuture<Index>> futureList, PlaylistVideoVo playlistVideoVo) {
        for (PlaylistVideoItemVo itemVo : playlistVideoVo.getItems()) {
            if (youtubeUtil.isPrivacyStatusUnusable(itemVo)) {
                continue;
            }
            String videoCode = itemVo.getSnippet().getResourceId().getVideoId();
            int videoIndex = itemVo.getSnippet().getPosition();
            CompletableFuture<Index> future = CompletableFuture.supplyAsync(() -> getIndex(videoIndex, videoCode));
            futureList.add(future);
        }

        return futureList.stream()
                .map(CompletableFuture::join)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private Index getIndex(int index, String videoCode) {
        Video video = getSearchedVideoLast(videoCode);

        if (!video.isUsable()) {
            return null;
        }

        return Index.builder()
                .index(index)
                .lectureTitle(unescapeHtml(video.getTitle()))
                .thumbnail(video.getThumbnail())
                .duration(video.getDuration())
                .build();
    }

    private Video getSearchedVideoLast(String lectureCode) {
        Optional<Video> videoOptional = lectureRepository.findVideo(lectureCode);

        Video video;
        if (videoOptional.isPresent() && dateUtil.validateExpiration(videoOptional.get().getUpdatedAt(), VIDEO_UPDATE_THRESHOLD_HOURS)) {
            video = videoOptional.get();
        } else {
            video = youtubeUtil.getVideoWithBlocking(lectureCode);
        }
        return video;
    }

    public VideoDetail getVideoDetail(Long memberId, String videoCode, int reviewLimit) {
        Mono<VideoVo> videoVoMono = youtubeApi.getVideoVo(VideoReq.builder()
                .videoCode(videoCode)
                .build());

        Set<String> enrolledVideoSet = lectureRepository.getVideosByMemberId(memberId);
        List<ReviewBrief> reviewList = lectureRepository.getReviewBriefList(videoCode, DEFAULT_REVIEW_OFFSET, reviewLimit);
        List<CourseBrief> courseBriefList = courseRepository.getCourseBriefListByMember(memberId);

        Video video = youtubeUtil.getVideoFromMono(videoVoMono);

        return VideoDetail.builder()
                .lectureCode(videoCode)
                .lectureTitle(unescapeHtml(video.getTitle()))
                .channel(video.getChannel())
                .description(unescapeHtml(video.getDescription()))
                .duration(video.getDuration())
                .playlist(false)
                .enrolled(enrolledVideoSet.contains(videoCode))
                .viewCount(video.getViewCount())
                .publishedAt(video.getPublishedAt().toLocalDateTime().toLocalDate().format(DateTimeFormatter.ISO_LOCAL_DATE))
                .thumbnail(video.getThumbnail())
                .reviews(reviewList)
                .reviewCount(reviewList.size())
                .rating(calculateAverageRating(reviewList))
                .courses(courseBriefList)
                .build();
    }

    private double calculateAverageRating(List<ReviewBrief> reviews) {
        if (reviews == null || reviews.isEmpty()) {
            return 0.0; // Return 0 if there are no reviews
        }

        return reviews.stream()
                .mapToInt(ReviewBrief::getSubmittedRating)
                .average()
                .orElse(0.0);
    }

    public <T> T blockFromMono(Mono<T> vo) {
        if (vo == null) {
            log.warn("youtube data api 실행에 실패하였습니다.");
            throw new RuntimeException();
        } else {
            return vo.block();
        }
    }

    public String unescapeHtml(String input) {
        return HtmlUtils.htmlUnescape(input);
    }

    public Recommendations getRecommendations(Long memberId) {
        HashSet<RecommendLecture> recommendLectureHashSet = new HashSet<>();
        List<RecommendLecture> recommendLectureList = new ArrayList<>();
        List<RecommendLecture> topRatedVideos = getTopRatedVideos();
        List<RecommendLecture> topRatedPlaylists = getTopRatedPlaylists();
        List<RecommendLecture> recommendLecturesByChannel = getRecommendsByChannel(memberId);

        Set<String> enrolledLectureSet = getEnrolledLectures(memberId);

        recommendLectureHashSet.addAll(topRatedVideos);
        recommendLectureHashSet.addAll(topRatedPlaylists);
        recommendLectureHashSet.addAll(recommendLecturesByChannel);

        for (String lectureCode : enrolledLectureSet) {
            recommendLectureHashSet.removeIf(recommendLecture -> (recommendLecture.getLectureCode().equals(lectureCode)));
        }

        recommendLectureList.addAll(recommendLectureHashSet);
        Collections.shuffle(recommendLectureList);

        Recommendations recommendations = Recommendations.builder()
                .recommendations(recommendLectureList)
                .build();

        return recommendations;
    }

    public List<RecommendLecture> getTopRatedVideos() {
        return lectureRepository.getVideosSortedByRating();
    }

    public List<RecommendLecture> getTopRatedPlaylists() {
        return lectureRepository.getPlaylistsSortedByRating();
    }

    public List<RecommendLecture> getRecommendsByChannel(Long memberId) {
        List<RecommendLecture> recommendLecturesByChannel = new ArrayList<>();
        List<String> channels = getMostEnrolledChannels(memberId);

        final int SELECT_BY_RANDOM_LIMIT = 1;
        final int SELECT_BY_PUBLISH_DATE_LIMIT = 2;
        final int SELECT_BY_VIEWED_LIMIT = 3;

        for (String channelName : channels) {
            List<RecommendLecture> randomVideosByChannel = lectureRepository.getRandomVideosByChannel(channelName, SELECT_BY_RANDOM_LIMIT);
            List<RecommendLecture> randomPlaylistsByChannel = lectureRepository.getRandomPlaylistsByChannel(channelName, SELECT_BY_RANDOM_LIMIT);

            List<RecommendLecture> mostViewedVideosByChannel = lectureRepository.getMostViewedVideosByChannel(channelName, SELECT_BY_VIEWED_LIMIT);
            List<RecommendLecture> mostViewedPlaylistsByChannel = lectureRepository.getMostViewedPlaylistsByChannel(channelName, SELECT_BY_VIEWED_LIMIT);

            List<RecommendLecture> latestPublishedVideosByChannel = lectureRepository.getLatestVideosByChannel(channelName, SELECT_BY_PUBLISH_DATE_LIMIT);
            List<RecommendLecture> latestPublishedPlaylistsByChannel = lectureRepository.getLatestPlaylistsByChannel(channelName, SELECT_BY_PUBLISH_DATE_LIMIT);

            recommendLecturesByChannel.addAll(randomVideosByChannel);
            recommendLecturesByChannel.addAll(randomPlaylistsByChannel);
            recommendLecturesByChannel.addAll(mostViewedPlaylistsByChannel);
            recommendLecturesByChannel.addAll(mostViewedVideosByChannel);
            recommendLecturesByChannel.addAll(latestPublishedVideosByChannel);
            recommendLecturesByChannel.addAll(latestPublishedPlaylistsByChannel);
        }

        return recommendLecturesByChannel;
    }

    public Set<String> getEnrolledLectures(Long memberId) {
        Set<String> lectureSet = new HashSet<>();
        lectureSet.addAll(lectureRepository.getVideosByMemberId(memberId));
        lectureSet.addAll(lectureRepository.getPlaylistByMemberId(memberId));
        return lectureSet;
    }

    public List<String> getMostEnrolledChannels(Long memberId) {
        return lectureRepository.getMostEnrolledChannels(memberId);
    }
}
