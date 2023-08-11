package com.m9d.sroom.lecture.service;

import com.m9d.sroom.course.domain.Playlist;
import com.m9d.sroom.course.domain.Video;
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
import com.m9d.sroom.util.youtube.vo.playlist.PlaylistSnippetVo;
import com.m9d.sroom.util.youtube.vo.playlist.PlaylistVo;
import com.m9d.sroom.util.youtube.vo.playlistitem.PlaylistVideoItemVo;
import com.m9d.sroom.util.youtube.vo.playlistitem.PlaylistVideoVo;
import com.m9d.sroom.util.youtube.vo.search.SearchItemVo;
import com.m9d.sroom.util.youtube.vo.search.SearchSnippetVo;
import com.m9d.sroom.util.youtube.vo.search.SearchVo;
import com.m9d.sroom.util.youtube.vo.video.VideoSnippetVo;
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
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static com.m9d.sroom.course.constant.CourseConstant.PLAYLIST_UPDATE_THRESHOLD_HOURS;
import static com.m9d.sroom.course.constant.CourseConstant.VIDEO_UPDATE_THRESHOLD_HOURS;
import static com.m9d.sroom.lecture.constant.LectureConstant.*;
import static com.m9d.sroom.util.DateUtil.FORMATTER;
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
        String encodedKeyword = URLEncoder.encode(keywordSearchParam.getKeyword(), StandardCharsets.UTF_8);
        LectureListReq lectureListReq = LectureListReq.builder()
                .keyword(encodedKeyword)
                .filter(keywordSearchParam.getFilter())
                .limit(keywordSearchParam.getLimit())
                .pageToken(keywordSearchParam.getNext_page_token())
                .build();

        Mono<SearchVo> searchVoMono = youtubeApi.getSearchVo(lectureListReq);
        Set<String> enrolledLectureSet = getLecturesByMemberId(memberId);

        return buildLectureListResponse(searchVoMono, enrolledLectureSet);
    }

    @Transactional
    public ResponseEntity<?> getLectureDetail(Long memberId, boolean isPlaylist, String lectureCode, LectureDetailParam lectureDetailParam) {
        lectureDetailParamValidate(isPlaylist, lectureDetailParam);
        log.info("lecture detail request. memberId = {}, lectureCode = {}", memberId, lectureCode);

        if (lectureDetailParam.isIndex_only()) {
            IndexInfo indexInfo = getPlaylistItems(lectureCode, lectureDetailParam.getIndex_next_token(), lectureDetailParam.getIndex_limit());
            return ResponseEntity.ok(indexInfo);
        }
        if (lectureDetailParam.isReview_only()) {
            List<ReviewBrief> reviewBriefList = getReviewBriefList(lectureCode, lectureDetailParam.getReview_offset(), lectureDetailParam.getReview_limit());
            return ResponseEntity.ok(reviewBriefList);
        }
        if (isPlaylist) {
            PlaylistDetail playlistDetail = getPlaylistDetail(memberId, lectureCode, lectureDetailParam.getIndex_next_token(), lectureDetailParam.getReview_limit());
            return ResponseEntity.ok(playlistDetail);
        }
        VideoDetail videoDetail = getVideoDetail(memberId, lectureCode, lectureDetailParam.getReview_limit());
        return ResponseEntity.ok(videoDetail);
    }

    private List<ReviewBrief> getReviewBriefList(String lectureCode, int reviewOffset, int reviewLimit) {
        return lectureRepository.getReviewBriefList(lectureCode, reviewOffset, reviewLimit);
    }

    private IndexInfo getPlaylistItems(String playlistCode, String nextPageToken, int indexLimit) {
        PlaylistItemReq playlistItemReq = PlaylistItemReq.builder()
                .playlistCode(playlistCode)
                .nextPageToken(nextPageToken)
                .limit(indexLimit)
                .build();

        Mono<PlaylistVideoVo> playlistVideoVoMono = youtubeApi.getPlaylistVideoVo(playlistItemReq);

        return buildIndexInfoResponse(playlistVideoVoMono);
    }

    public VideoDetail getVideoDetail(Long memberId, String videoCode, int reviewLimit) {
        Mono<VideoVo> videoVoMono = youtubeApi.getVideoVo(VideoReq.builder()
                .videoCode(videoCode)
                .build());

        return buildVideoDetailResponse(memberId, videoCode, videoVoMono, reviewLimit);
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

        return buildPlaylistDetailResponse(memberId, playlistCode, playlistVoMono, playlistVideoVoMono, reviewLimit);
    }

    private PlaylistDetail buildPlaylistDetailResponse(Long memberId, String playlistCode, Mono<PlaylistVo> playlistVoMono, Mono<PlaylistVideoVo> playlistVideoVoMono, int reviewLimit) {
        Set<String> enrolledPlaylistSet = getEnrolledPlaylistByMemberId(memberId);

        PlaylistVo playlistVo = safeGetVo(playlistVoMono);
        PlaylistSnippetVo snippetVo = playlistVo.getItems().get(FIRST_INDEX).getSnippet();
        String thumbnail = youtubeUtil.selectThumbnailInVo(snippetVo.getThumbnails());

        IndexInfo indexInfo = buildIndexInfoResponse(playlistVideoVoMono);
        List<CourseBrief> courseBriefList = courseRepository.getCourseBriefListByMember(memberId);

        return PlaylistDetail.builder()
                .lectureCode(playlistCode)
                .lectureTitle(snippetVo.getTitle())
                .channel(snippetVo.getChannelTitle())
                .description(snippetVo.getDescription())
                .playlist(true)
                .publishedAt(snippetVo.getPublishedAt().substring(PUBLISHED_DATE_START_INDEX, PUBLISHED_DATE_END_INDEX))
                .enrolled(enrolledPlaylistSet.contains(playlistCode))
                .lectureCount(playlistVo.getItems().get(FIRST_INDEX).getContentDetails().getItemCount())
                .thumbnail(thumbnail)
                .indexes(indexInfo)
                .duration(indexInfo.getTotalDuration())
                .reviews(lectureRepository.getReviewBriefList(playlistCode, DEFAULT_REVIEW_OFFSET, reviewLimit))
                .courses(courseBriefList)
                .build();
    }

    private IndexInfo buildIndexInfoResponse(Mono<PlaylistVideoVo> playlistVideoVoMono) {
        AtomicInteger totalDurationSeconds = new AtomicInteger(0);
        List<CompletableFuture<Index>> futureList = new ArrayList<>();
        PlaylistVideoVo playlistVideoVo = safeGetVo(playlistVideoVoMono);
        log.debug("index output = {}", playlistVideoVo.toString());

        for (PlaylistVideoItemVo itemVo : playlistVideoVo.getItems()) {
            if (youtubeUtil.isPrivacyStatusUnusable(itemVo)) {
                continue;
            }
            String videoCode = itemVo.getSnippet().getResourceId().getVideoId();
            int videoIndex = itemVo.getSnippet().getPosition();
            CompletableFuture<Index> future = CompletableFuture.supplyAsync(() -> buildIndex(videoIndex, videoCode));
            futureList.add(future);
        }

        List<Index> indexList = futureList.stream().map(CompletableFuture::join).collect(Collectors.toList());

        for (Index index : indexList) {
            totalDurationSeconds.addAndGet(index.getDuration());
        }

        String nextPageToken = Optional.of(playlistVideoVo)
                .map(PlaylistVideoVo::getNextPageToken)
                .orElse(null);
        int totalDuration = totalDurationSeconds.get();

        return IndexInfo.builder()
                .indexList(indexList)
                .totalDuration(totalDuration)
                .nextPageToken(nextPageToken)
                .build();
    }

    private Index buildIndex(int index, String videoCode) {
        Video video = getVideoLast(videoCode);
        return Index.builder()
                .index(index)
                .lectureTitle(unescapeHtml(video.getTitle()))
                .thumbnail(video.getThumbnail())
                .duration(video.getDuration())
                .build();
    }

    private VideoDetail buildVideoDetailResponse(Long memberId, String videoCode, Mono<VideoVo> videoVoMono, int reviewLimit) {
        Set<String> enrolledVideoSet = getEnrolledVideoByMemberId(memberId);
        List<ReviewBrief> reviewBriefList = lectureRepository.getReviewBriefList(videoCode, DEFAULT_REVIEW_OFFSET, reviewLimit);
        List<CourseBrief> courseBriefList = courseRepository.getCourseBriefListByMember(memberId);

        VideoVo videoVo = safeGetVo(videoVoMono);
        VideoSnippetVo snippetVo = videoVo.getItems().get(FIRST_INDEX).getSnippet();
        String thumbnail = youtubeUtil.selectThumbnailInVo(snippetVo.getThumbnails());
        int videoDuration = dateUtil.convertISOToSeconds(videoVo.getItems().get(FIRST_INDEX).getContentDetails().getDuration());

        return VideoDetail.builder()
                .lectureCode(videoCode)
                .lectureTitle(unescapeHtml(snippetVo.getTitle()))
                .channel(snippetVo.getChannelTitle())
                .description(unescapeHtml(snippetVo.getDescription()))
                .duration(videoDuration)
                .playlist(false)
                .enrolled(enrolledVideoSet.contains(videoCode))
                .viewCount(videoVo.getItems().get(FIRST_INDEX).getStatistics().getViewCount())
                .publishedAt(snippetVo.getPublishedAt().substring(PUBLISHED_DATE_START_INDEX, PUBLISHED_DATE_END_INDEX))
                .thumbnail(thumbnail)
                .reviews(reviewBriefList)
                .reviewCount(reviewBriefList.size())
                .courses(courseBriefList)
                .build();
    }


    public Set<String> getLecturesByMemberId(Long memberId) {
        Set<String> lectureSet = new HashSet<>();
        lectureSet.addAll(getEnrolledVideoByMemberId(memberId));
        lectureSet.addAll(getEnrolledPlaylistByMemberId(memberId));
        return lectureSet;
    }

    public Set<String> getEnrolledVideoByMemberId(Long memberId) {
        return lectureRepository.getVideosByMemberId(memberId);
    }

    public Set<String> getEnrolledPlaylistByMemberId(Long memberId) {
        return lectureRepository.getPlaylistByMemberId(memberId);
    }

    public KeywordSearch buildLectureListResponse(Mono<SearchVo> searchVoMono, Set<String> enrolledLectureSet) {
        SearchVo searchVo = safeGetVo(searchVoMono);

        List<CompletableFuture<Lecture>> futures = searchVo.getItems().stream()
                .map(item -> CompletableFuture.supplyAsync(() -> getLecture(enrolledLectureSet, item)))
                .collect(Collectors.toList());
        List<Lecture> lectures = futures.stream()
                .map(CompletableFuture::join)
                .collect(Collectors.toList());

        String nextPageToken = Optional.of(searchVo)
                .map(SearchVo::getNextPageToken)
                .orElse(null);
        return KeywordSearch.builder()
                .nextPageToken(nextPageToken)
                .resultPerPage(searchVo.getPageInfo().getResultPerPage())
                .lectures(lectures)
                .build();
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
            Playlist playlist = getPlaylistLast(lectureCode);
            videoCount = playlist.getLectureCount();
            description = playlist.getDescription();
        } else {
            lectureCode = item.getId().getVideoId();
            Video video = getVideoLast(lectureCode);
            viewCount = video.getViewCount();
            description = video.getDescription();
        }

        return Lecture.builder()
                .lectureTitle(unescapeHtml(snippetVo.getTitle()))
                .description(unescapeHtml(description))
                .channel(unescapeHtml(snippetVo.getChannelTitle()))
                .lectureCode(lectureCode)
                .enrolled(enrolledLectureSet.contains(lectureCode))
                .publishedAt(ZonedDateTime.parse(snippetVo.getPublishTime()).format(FORMATTER))
                .playlist(isPlaylist)
                .lectureCount(videoCount)
                .viewCount(viewCount)
                .thumbnail(thumbnail)
                .build();
    }

    private Video getVideoLast(String lectureCode) {
        Optional<Video> videoOptional = lectureRepository.findVideo(lectureCode);

        Video video;
        if (videoOptional.isPresent() && dateUtil.validateExpiration(videoOptional.get().getUpdatedAt(), VIDEO_UPDATE_THRESHOLD_HOURS)) {
            video = videoOptional.get();
        } else {
            Mono<VideoVo> videoVoMono = youtubeApi.getVideoVo(VideoReq.builder()
                    .videoCode(lectureCode)
                    .build());
            VideoVo videoVo = safeGetVo(videoVoMono);
            String thumbnail = youtubeUtil.selectThumbnailInVo(videoVo.getItems().get(FIRST_INDEX).getSnippet().getThumbnails());
            video = Video.builder()
                    .title(videoVo.getItems().get(FIRST_INDEX).getSnippet().getTitle())
                    .videoCode(lectureCode)
                    .viewCount(videoVo.getItems().get(FIRST_INDEX).getStatistics().getViewCount())
                    .description(videoVo.getItems().get(FIRST_INDEX).getSnippet().getDescription())
                    .thumbnail(thumbnail)
                    .duration(dateUtil.convertISOToSeconds(videoVo.getItems().get(FIRST_INDEX).getContentDetails().getDuration()))
                    .build();
        }
        return video;
    }

    private Playlist getPlaylistLast(String lectureCode) {
        Optional<Playlist> playlistOptional = lectureRepository.findVideoCountAndDescription(lectureCode);

        Playlist playlist;
        if (playlistOptional.isPresent() && dateUtil.validateExpiration(playlistOptional.get().getUpdatedAt(), PLAYLIST_UPDATE_THRESHOLD_HOURS)) {
            playlist = playlistOptional.get();
        } else {
            Mono<PlaylistVo> playlistVoMono = youtubeApi.getPlaylistVo(PlaylistReq.builder()
                    .playlistCode(lectureCode)
                    .build());
            PlaylistVo playlistVo = safeGetVo(playlistVoMono);
            playlist = Playlist.builder()
                    .playlistCode(lectureCode)
                    .description(playlistVo.getItems().get(FIRST_INDEX).getSnippet().getDescription())
                    .lectureCount(playlistVo.getItems().get(FIRST_INDEX).getContentDetails().getItemCount())
                    .build();
        }

        return playlist;
    }

    public <T> T safeGetVo(Mono<T> vo) {
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

    public void lectureDetailParamValidate(boolean isPlaylist, LectureDetailParam lectureDetailParam) {
        if (lectureDetailParam.isIndex_only() && lectureDetailParam.isReview_only()) {
            throw new TwoOnlyParamTrueException();
        }
        if (!isPlaylist && lectureDetailParam.isIndex_only()) {
            throw new VideoIndexParamException();
        }
    }

    public Recommendations getRecommendations(Long memberId) {
        HashSet<RecommendLecture> recommendLectureHashSet = new HashSet<>();
        List<RecommendLecture> recommendLectureList = new ArrayList<>();
        List<RecommendLecture> topRatedVideos = getTopRatedVideos();
        List<RecommendLecture> topRatedPlaylists = getTopRatedPlaylists();
        List<RecommendLecture> recommendLecturesByChannel = getRecommendsByChannel(memberId);

        Set<String> enrolledLectureSet = getLecturesByMemberId(memberId);

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

    public List<String> getMostEnrolledChannels(Long memberId) {
        return lectureRepository.getMostEnrolledChannels(memberId);
    }
}
