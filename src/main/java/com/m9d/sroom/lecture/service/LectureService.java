package com.m9d.sroom.lecture.service;

import com.fasterxml.jackson.databind.JsonNode;
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
import com.m9d.sroom.lecture.exception.VideoNotFoundException;
import com.m9d.sroom.lecture.repository.LectureRepository;
import com.m9d.sroom.util.DateUtil;
import com.m9d.sroom.util.youtube.YoutubeUtil;
import com.m9d.sroom.util.youtube.resource.LectureListReq;
import com.m9d.sroom.util.youtube.resource.PlaylistReq;
import com.m9d.sroom.util.youtube.resource.PlaylistItemReq;
import com.m9d.sroom.util.youtube.resource.VideoReq;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.HtmlUtils;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

import static com.m9d.sroom.course.constant.CourseConstant.PLAYLIST_UPDATE_THRESHOLD_HOURS;
import static com.m9d.sroom.course.constant.CourseConstant.VIDEO_UPDATE_THRESHOLD_HOURS;
import static com.m9d.sroom.lecture.constant.LectureConstant.*;
import static com.m9d.sroom.util.youtube.YoutubeConstant.*;

@Service
@Slf4j
public class LectureService {

    private final LectureRepository lectureRepository;
    private final CourseRepository courseRepository;
    private final YoutubeUtil youtubeUtil;
    private final DateUtil dateUtil;

    public LectureService(LectureRepository lectureRepository, CourseRepository courseRepository, YoutubeUtil youtubeUtil, DateUtil dateUtil) {
        this.lectureRepository = lectureRepository;
        this.courseRepository = courseRepository;
        this.youtubeUtil = youtubeUtil;
        this.dateUtil = dateUtil;
    }

    @Transactional
    public KeywordSearch searchByKeyword(Long memberId, KeywordSearchParam keywordSearchParam) {
        LectureListReq lectureListReq = LectureListReq.builder()
                .keyword(keywordSearchParam.getKeyword())
                .filter(keywordSearchParam.getFilter())
                .limit(keywordSearchParam.getLimit())
                .pageToken(keywordSearchParam.getNext_page_token())
                .build();

        JsonNode resultNode = youtubeUtil.safeGet(youtubeUtil.getYoutubeResource(lectureListReq));
        Set<String> enrolledLectureSet = youtubeUtil.safeGet(getLecturesByMemberId(memberId));

        KeywordSearch keywordSearch = buildLectureListResponse(resultNode, enrolledLectureSet);
        return keywordSearch;
    }

    public VideoDetail getVideoDetail(Long memberId, String lectureCode, int reviewLimit) {
        VideoReq videoReq = VideoReq.builder()
                .videoCode(lectureCode)
                .build();
        JsonNode resultNode = youtubeUtil.safeGet(youtubeUtil.getYoutubeResource(videoReq));

        if (resultNode.get(JSONNODE_PAGE_INFO).get(JSONNODE_TOTAL_RESULTS).asInt() == 0) {
            throw new VideoNotFoundException();
        }

        Set<String> enrolledVideoSet = getEnrolledVideoByMemberId(memberId);

        VideoDetail videoDetail = buildVideoDetailResponse(resultNode, reviewLimit, enrolledVideoSet);
        videoDetail.setCourses(courseRepository.getCourseBriefListByMember(memberId));
        return videoDetail;
    }

    public PlaylistDetail getPlaylistDetail(Long memberId, String lectureCode, String indexNextToken, int reviewLimit) {
        PlaylistReq playlistReq = PlaylistReq.builder()
                .playlistCode(lectureCode)
                .build();
        PlaylistItemReq playlistItemReq = PlaylistItemReq.builder()
                .playlistCode(lectureCode)
                .nextPageToken(indexNextToken)
                .limit(DEFAULT_INDEX_COUNT)
                .build();

        CompletableFuture<JsonNode> playlistFuture = youtubeUtil.getYoutubeResource(playlistReq);
        CompletableFuture<JsonNode> indexFuture = youtubeUtil.getYoutubeResource(playlistItemReq);
        CompletableFuture.allOf(playlistFuture, indexFuture).join();

        JsonNode playlistNode = youtubeUtil.safeGet(playlistFuture);
        JsonNode indexNode = youtubeUtil.safeGet(indexFuture);

        youtubeUtil.validateNodeIfNotFound(playlistNode);
        youtubeUtil.validateNodeIfNotFound(indexNode);

        Set<String> enrolledPlaylistSet = getEnrolledPlaylistByMemberId(memberId);

        PlaylistDetail playlistDetail = buildPlaylistDetailResponse(playlistNode, indexNode, reviewLimit, enrolledPlaylistSet);
        playlistDetail.setCourses(courseRepository.getCourseBriefListByMember(memberId));
        return playlistDetail;
    }

    public IndexInfo getPlaylistItems(String lectureCode, String indexNextToken, int indexLimit) {
        PlaylistItemReq playlistItemReq = PlaylistItemReq.builder()
                .playlistCode(lectureCode)
                .nextPageToken(indexNextToken)
                .limit(indexLimit)
                .build();
        JsonNode resultNode = youtubeUtil.safeGet(youtubeUtil.getYoutubeResource(playlistItemReq));
        youtubeUtil.validateNodeIfNotFound(resultNode);

        IndexInfo indexInfo = buildIndexInfoResponse(resultNode);
        return indexInfo;
    }

    public List<ReviewBrief> getReviewBriefList(String lectureCode, int reviewOffset, int reviewLimit) {
        List<ReviewBrief> reviewBriefList = lectureRepository.getReviewBriefList(lectureCode, reviewOffset, reviewLimit);
        return reviewBriefList;
    }

    @Async
    public CompletableFuture<Set<String>> getLecturesByMemberId(Long memberId) {
        Set<String> lectureSet = new HashSet<>();
        lectureSet.addAll(getEnrolledVideoByMemberId(memberId));
        lectureSet.addAll(getEnrolledPlaylistByMemberId(memberId));
        return CompletableFuture.completedFuture(lectureSet);
    }

    public Set<String> getEnrolledVideoByMemberId(Long memberId) {
        return lectureRepository.getVideosByMemberId(memberId);
    }

    public Set<String> getEnrolledPlaylistByMemberId(Long memberId) {
        return lectureRepository.getPlaylistByMemberId(memberId);
    }

    public KeywordSearch buildLectureListResponse(JsonNode resultNode, Set<String> enrolledLectureSet) {
        List<Lecture> lectureList = new ArrayList<>();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        for (JsonNode item : resultNode.get(JSONNODE_ITEMS)) {
            JsonNode snippetNode = item.get(JSONNODE_SNIPPET);
            boolean isPlaylist = item.get(JSONNODE_ID).get(JSONNODE_KIND).asText().equals(JSONNODE_TYPE_PLAYLIST);
            String thumbnail = youtubeUtil.selectThumbnail(snippetNode.get(JSONNODE_THUMBNAILS));

            String lectureCode;
            Long viewCount = 0L;
            int videoCount = 1;
            String lectureTitle = snippetNode.get(JSONNODE_TITLE).asText();
            String channel = snippetNode.get(JSONNODE_CHANNEL_TITLE).asText();
            String description;

            if (isPlaylist) {
                lectureCode = item.get(JSONNODE_ID).get(JSONNODE_PLAYLIST_ID).asText();
                Playlist playlist = getPlaylistItemCountAndDescription(lectureCode);
                videoCount = playlist.getLectureCount();
                description = playlist.getDescription();
            } else {
                lectureCode = item.get(JSONNODE_ID).get(JSONNODE_VIDEO_ID).asText();
                Video video = getViewCountAndDescription(lectureCode);
                viewCount = video.getViewCount();
                description = video.getDescription();
            }

            boolean isEnrolled = enrolledLectureSet.contains(lectureCode);
            ZonedDateTime date = ZonedDateTime.parse(snippetNode.get(JSONNODE_PUBLISHETIME).asText());
            String formatted = date.format(formatter);

            Lecture lecture = Lecture.builder()
                    .lectureTitle(unescapeHtml(lectureTitle))
                    .description(unescapeHtml(description))
                    .channel(unescapeHtml(channel))
                    .lectureCode(lectureCode)
                    .enrolled(isEnrolled)
                    .publishedAt(formatted)
                    .playlist(isPlaylist)
                    .lectureCount(videoCount)
                    .viewCount(viewCount)
                    .thumbnail(thumbnail)
                    .build();
            lectureList.add(lecture);
        }

        String nextPageToken = resultNode.has(JSONNODE_NEXT_PAGE_TOKEN) ? resultNode.get("nextPageToken").asText() : null;
        String prevPageToken = resultNode.has(JSONNODE_PREV_PAGE_TOKEN) ? resultNode.get("prevPageToken").asText() : null;

        KeywordSearch keywordSearch = KeywordSearch.builder()
                .nextPageToken(nextPageToken)
                .prevPageToken(prevPageToken)
                .resultPerPage(resultNode.get(JSONNODE_PAGE_INFO).get(JSONNODE_RESULT_PER_PAGE).asInt())
                .lectures(lectureList)
                .build();
        return keywordSearch;
    }

    private Video getViewCountAndDescription(String lectureCode) {
        Optional<Video> videoOptional = lectureRepository.findViewCountAndDescriptioin(lectureCode);

        Video video;
        if (videoOptional.isPresent() && dateUtil.validateExpiration(videoOptional.get().getUpdatedAt(), VIDEO_UPDATE_THRESHOLD_HOURS)) {
            video = videoOptional.get();
        } else {
            JsonNode videoNode = youtubeUtil.safeGet(youtubeUtil.getYoutubeResource(VideoReq.
                    builder().
                    videoCode(lectureCode).
                    build()));
            video = Video.builder()
                    .videoCode(lectureCode)
                    .viewCount(videoNode.get(JSONNODE_ITEMS).get(FIRST_INDEX).get(JSONNODE_STATISTICS).get(JSONNODE_VIEW_COUNT).asLong())
                    .description(videoNode.get(JSONNODE_ITEMS).get(FIRST_INDEX).get(JSONNODE_SNIPPET).get(JSONNODE_DESCRIPTION).asText())
                    .build();
        }

        return video;
    }

    private Playlist getPlaylistItemCountAndDescription(String lectureCode) {
        Optional<Playlist> playlistOptional = lectureRepository.findVideoCountAndDescription(lectureCode);

        Playlist playlist;
        if (playlistOptional.isPresent() && dateUtil.validateExpiration(playlistOptional.get().getUpdatedAt(), PLAYLIST_UPDATE_THRESHOLD_HOURS)) {
            playlist = playlistOptional.get();
        } else {
            JsonNode playlistNode = youtubeUtil.safeGet(youtubeUtil.getYoutubeResource(PlaylistReq.builder()
                    .playlistCode(lectureCode)
                    .build()));
            playlist = Playlist.builder()
                    .playlistCode(lectureCode)
                    .description(playlistNode.get(JSONNODE_ITEMS).get(FIRST_INDEX).get(JSONNODE_SNIPPET).get(JSONNODE_DESCRIPTION).asText())
                    .lectureCount(playlistNode.get(JSONNODE_ITEMS).get(FIRST_INDEX).get(JSONNODE_CONTENT_DETAIL).get(JSONNODE_ITEM_COUNT).asInt())
                    .build();
        }

        return playlist;
    }

    public VideoDetail buildVideoDetailResponse(JsonNode resultNode, int reviewLimit, Set<String> enrolledVideoSet) {
        JsonNode snippetJsonNode = resultNode
                .get(JSONNODE_ITEMS)
                .get(FIRST_INDEX)
                .get(JSONNODE_SNIPPET);
        String thumbnail = youtubeUtil.selectThumbnail(snippetJsonNode.get(JSONNODE_THUMBNAILS));

        List<ReviewBrief> reviewBriefList = lectureRepository.getReviewBriefList(resultNode.get(JSONNODE_ITEMS).get(FIRST_INDEX).get(JSONNODE_ID).asText(), DEFAULT_REVIEW_OFFSET, reviewLimit);

        String lectureCode = resultNode.get(JSONNODE_ITEMS).get(FIRST_INDEX).get(JSONNODE_ID).asText();
        int videoDuration = dateUtil.convertISOToSeconds(resultNode.get(JSONNODE_ITEMS).get(FIRST_INDEX).get(JSONNODE_CONTENT_DETAIL).get(JSONNODE_DURATION).asText());

        boolean isEnrolled = enrolledVideoSet.contains(lectureCode);
        String lectureTitle = snippetJsonNode.get(JSONNODE_TITLE).asText();
        String channel = snippetJsonNode.get(JSONNODE_CHANNEL_TITLE).asText();
        String description = snippetJsonNode.get(JSONNODE_DESCRIPTION).asText();

        VideoDetail videoDetail = VideoDetail.builder()
                .lectureCode(lectureCode)
                .lectureTitle(unescapeHtml(lectureTitle))
                .channel(unescapeHtml(channel))
                .description(unescapeHtml(description))
                .duration(videoDuration)
                .playlist(false)
                .enrolled(isEnrolled)
                .viewCount(resultNode.get(JSONNODE_ITEMS).get(FIRST_INDEX).get(JSONNODE_STATISTICS).get(JSONNODE_VIEW_COUNT).asLong())
                .publishedAt(snippetJsonNode.get(JSONNODE_PUBLISHED_AT).asText().substring(PUBLISHED_DATE_START_INDEX, PUBLISHED_DATE_END_INDEX))
                .thumbnail(thumbnail)
                .reviews(reviewBriefList)
                .reviewCount(reviewBriefList.size())
                .build();

        return videoDetail;
    }

    public PlaylistDetail buildPlaylistDetailResponse(JsonNode playlistNode, JsonNode indexNode, int reviewLimit, Set<String> enrolledPlaylistSet) {
        JsonNode snippetJsonNode = playlistNode.get(JSONNODE_ITEMS).get(FIRST_INDEX).get(JSONNODE_SNIPPET);
        String thumbnail = youtubeUtil.selectThumbnail(playlistNode.get(JSONNODE_ITEMS).get(FIRST_INDEX).get(JSONNODE_SNIPPET).get(JSONNODE_THUMBNAILS));

        IndexInfo indexInfo = buildIndexInfoResponse(indexNode);
        List<ReviewBrief> reviewBriefList = lectureRepository.getReviewBriefList(playlistNode.get(JSONNODE_ITEMS).get(FIRST_INDEX).get(JSONNODE_ID).asText(), DEFAULT_REVIEW_OFFSET, reviewLimit);

        String lectureCode = playlistNode.get(JSONNODE_ITEMS).get(FIRST_INDEX).get(JSONNODE_ID).asText();
        boolean isEnrolled = enrolledPlaylistSet.contains(lectureCode);
        String lectureTitle = snippetJsonNode.get(JSONNODE_TITLE).asText();
        String channel = snippetJsonNode.get(JSONNODE_CHANNEL_TITLE).asText();
        String description = snippetJsonNode.get(JSONNODE_DESCRIPTION).asText();
        String publishedAt = snippetJsonNode.get(JSONNODE_PUBLISHED_AT).asText();

        return PlaylistDetail.builder()
                .lectureCode(lectureCode)
                .lectureTitle(unescapeHtml(lectureTitle))
                .channel(unescapeHtml(channel))
                .description(unescapeHtml(description))
                .playlist(true)
                .publishedAt(publishedAt)
                .enrolled(isEnrolled)
                .lectureCount(playlistNode.get(JSONNODE_ITEMS).get(FIRST_INDEX).get(JSONNODE_CONTENT_DETAIL).get(JSONNODE_ITEM_COUNT).asInt())
                .thumbnail(thumbnail)
                .indexes(indexInfo)
                .reviews(reviewBriefList)
                .build();
    }

    public IndexInfo buildIndexInfoResponse(JsonNode indexNode) {
        String nextPageToken = indexNode.has(JSONNODE_NEXT_PAGE_TOKEN) ? indexNode.get(JSONNODE_NEXT_PAGE_TOKEN).asText() : null;

        AtomicLong totalDurationSeconds = new AtomicLong();

        List<CompletableFuture<Index>> futuresList = new ArrayList<>();
        for (JsonNode item : indexNode.get(JSONNODE_ITEMS)) {
            futuresList.add(buildIndex(item, totalDurationSeconds));
        }

        CompletableFuture<Void> allFutures = CompletableFuture.allOf(futuresList.toArray(new CompletableFuture[futuresList.size()]));

        allFutures.join();

        List<Index> indexList = futuresList.stream()
                .map(CompletableFuture::join)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        int totalDuration = totalDurationSeconds.intValue();

        return IndexInfo.builder()
                .indexList(indexList)
                .totalDuration(totalDuration)
                .nextPageToken(nextPageToken)
                .build();
    }

    private CompletableFuture<Index> buildIndex(JsonNode item, AtomicLong totalDurationSeconds) {
        JsonNode snippetNode = item.get(JSONNODE_SNIPPET);
        String lectureCode = snippetNode.get(JSONNODE_RESOURCE_ID).get(JSONNODE_VIDEO_ID).asText();

        return CompletableFuture.supplyAsync(() -> {
            JsonNode videoNode = youtubeUtil.safeGet(youtubeUtil.getYoutubeResource(VideoReq.builder().videoCode(lectureCode).build()));

            if (snippetNode.get(JSONNODE_RESOURCE_ID).get(JSONNODE_KIND).asText().equals(JSONNODE_TYPE_VIDEO) && item.get(JSONNODE_STATUS).get(JSONNODE_PRIVACY_STATUS).asText().equals(JSONNODE_PUBLIC)) {
                String videoDuration = videoNode.get(JSONNODE_ITEMS).get(FIRST_INDEX).get(JSONNODE_CONTENT_DETAIL).get(JSONNODE_DURATION).asText();
                totalDurationSeconds.addAndGet(Duration.parse(videoDuration).getSeconds());
                String thumbnail = youtubeUtil.selectThumbnail(item.get(JSONNODE_SNIPPET).get(JSONNODE_THUMBNAILS));
                return Index.builder()
                        .index(snippetNode.get(JSONNODE_POSITION).asInt())
                        .lectureTitle(unescapeHtml(snippetNode.get(JSONNODE_TITLE).asText()))
                        .thumbnail(thumbnail)
                        .duration(dateUtil.convertISOToSeconds(videoDuration))
                        .build();
            }
            return null;
        });
    }

    public String unescapeHtml(String input) {
        return HtmlUtils.htmlUnescape(input);
    }

    @Transactional
    public ResponseEntity<?> getLectureDetail(Long memberId, boolean isPlaylist, String
            lectureCode, LectureDetailParam lectureDetailParam) {
        lectureDetailParamValidate(isPlaylist, lectureDetailParam);

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

    public void lectureDetailParamValidate(boolean isPlaylist, LectureDetailParam lectureDetailParam) {
        if (lectureDetailParam.isIndex_only() && lectureDetailParam.isReview_only()) {
            throw new TwoOnlyParamTrueException();
        }
        if (!isPlaylist && lectureDetailParam.isIndex_only()) {
            throw new VideoIndexParamException();
        }
    }

    public Recommendations getRecommendations(Long memberId) {
        return null;
    }

    public List<RecommendLecture> getTopRatedVideos() {
        return null;
    }

    public List<RecommendLecture> getTopRatedPlaylists() {
        return null;
    }

    public List<RecommendLecture> getRecommendsByChannel(Long memberId) {
        return null;
    }

    public List<String> getMostEnrolledChannels(Long memberId) {
        return null;
    }
}
