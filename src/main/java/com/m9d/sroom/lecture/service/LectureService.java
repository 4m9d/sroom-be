package com.m9d.sroom.lecture.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.m9d.sroom.lecture.dto.request.KeywordSearchParam;
import com.m9d.sroom.lecture.dto.request.LectureDetailParam;
import com.m9d.sroom.lecture.dto.response.Index;
import com.m9d.sroom.lecture.dto.response.Lecture;
import com.m9d.sroom.lecture.dto.response.ReviewBrief;
import com.m9d.sroom.lecture.dto.response.*;
import com.m9d.sroom.lecture.exception.LectureNotFoundException;
import com.m9d.sroom.lecture.exception.TwoOnlyParamTrueException;
import com.m9d.sroom.lecture.exception.VideoIndexParamException;
import com.m9d.sroom.lecture.exception.VideoNotFoundException;
import com.m9d.sroom.lecture.repository.LectureRepository;
import com.m9d.sroom.util.youtube.YoutubeUtil;
import com.m9d.sroom.util.youtube.resource.LectureList;
import com.m9d.sroom.util.youtube.resource.Playlist;
import com.m9d.sroom.util.youtube.resource.PlaylistItem;
import com.m9d.sroom.util.youtube.resource.Video;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

import static com.m9d.sroom.lecture.constant.LectureConstant.*;

@Service
@Slf4j
public class LectureService {

    private final LectureRepository lectureRepository;
    private final YoutubeUtil youtubeUtil;

    public LectureService(LectureRepository lectureRepository, YoutubeUtil youtubeUtil) {
        this.lectureRepository = lectureRepository;
        this.youtubeUtil = youtubeUtil;
    }

    public KeywordSearch searchByKeyword(Long memberId, KeywordSearchParam keywordSearchParam) {
        LectureList lectureList = LectureList.builder()
                .keyword(keywordSearchParam.getKeyword())
                .filter(keywordSearchParam.getFilter())
                .limit(keywordSearchParam.getLimit())
                .pageToken(keywordSearchParam.getNextPageToken())
                .build();

        JsonNode resultNode = safeGet(youtubeUtil.getYoutubeResource(lectureList));
        Set<String> enrolledLectureSet = safeGet(getLecturesByMemberId(memberId));

        KeywordSearch keywordSearch = buildLectureListResponse(resultNode, enrolledLectureSet);
        return keywordSearch;
    }

    public VideoDetail getVideoDetail(Long memberId, String lectureCode, int reviewLimit) {
        Video video = Video.builder()
                .videoId(lectureCode)
                .build();
        JsonNode resultNode = safeGet(youtubeUtil.getYoutubeResource(video));

        if (resultNode.get(JSONNODE_PAGE_INFO).get(JSONNODE_TOTAL_RESULTS).asInt() == 0) {
            throw new VideoNotFoundException();
        }

        Set<String> enrolledVideoSet = getEnrolledVideoByMemberId(memberId);

        VideoDetail videoDetail = buildVideoDetailResponse(resultNode, reviewLimit, enrolledVideoSet);
        return videoDetail;
    }

    public PlaylistDetail getPlaylistDetail(Long memberId, String lectureCode, String indexNextToken, int reviewLimit) {
        Playlist playlist = Playlist.builder()
                .playlistId(lectureCode)
                .build();
        PlaylistItem playlistItem = PlaylistItem.builder()
                .playlistId(lectureCode)
                .nextPageToken(indexNextToken)
                .limit(DEFAULT_REVIEW_COUNT)
                .build();

        CompletableFuture<JsonNode> playlistFuture = youtubeUtil.getYoutubeResource(playlist);
        CompletableFuture<JsonNode> indexFuture = youtubeUtil.getYoutubeResource(playlistItem);
        CompletableFuture.allOf(playlistFuture, indexFuture).join();

        JsonNode playlistNode = safeGet(playlistFuture);
        JsonNode indexNode = safeGet(indexFuture);

        validateNodeIfNotFound(playlistNode);
        validateNodeIfNotFound(indexNode);

        Set<String> enrolledPlaylistSet = getEnrolledPlaylistByMemberId(memberId);

        PlaylistDetail playlistDetail = buildPlaylistDetailResponse(playlistNode, indexNode, reviewLimit, enrolledPlaylistSet);
        return playlistDetail;
    }

    public void validateNodeIfNotFound(JsonNode node) {
        if (node.get("pageInfo").get("totalResults").asInt() == 0) {
            LectureNotFoundException e = new LectureNotFoundException();
            log.info("error occurred. message = {}", e.getMessage(), e);
            throw e;
        }
    }


    public IndexInfo getPlaylistItems(String lectureCode, String indexNextToken, int indexLimit) {
        PlaylistItem playlistItem = PlaylistItem.builder()
                .playlistId(lectureCode)
                .nextPageToken(indexNextToken)
                .limit(indexLimit)
                .build();
        JsonNode resultNode = safeGet(youtubeUtil.getYoutubeResource(playlistItem));
        validateNodeIfNotFound(resultNode);

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

        for (JsonNode item : resultNode.get(JSONNODE_ITEMS)) {
            System.out.println(item.toPrettyString());
            JsonNode snippetNode = item.get(JSONNODE_SNIPPET);
            boolean isPlaylist = item.get(JSONNODE_ID).get(JSONNODE_KIND).asText().equals(JSONNODE_TYPE_PLAYLIST);
            String thumbnail = selectThumbnail(snippetNode.get(JSONNODE_THUMBNAILS));

            String lectureCode;
            if (isPlaylist) {
                lectureCode = item.get(JSONNODE_ID).get(JSONNODE_PLAYLIST_ID).asText();
            } else {
                lectureCode = item.get(JSONNODE_ID).get(JSONNODE_VIDEO_ID).asText();
            }
            boolean isEnrolled = enrolledLectureSet.contains(lectureCode);
            String lectureTitle = snippetNode.get(JSONNODE_TITLE).asText();
            String channel = snippetNode.get(JSONNODE_CHANNEL_TITLE).asText();
            String description = snippetNode.get(JSONNODE_DESCRIPTION).asText();

            Lecture lecture = Lecture.builder()
                    .lectureTitle(unescapeHtml(lectureTitle))
                    .description(unescapeHtml(description))
                    .channel(unescapeHtml(channel))
                    .lectureCode(lectureCode)
                    .enrolled(isEnrolled)
                    .playlist(isPlaylist)
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

    public VideoDetail buildVideoDetailResponse(JsonNode resultNode, int reviewLimit, Set<String> enrolledVideoSet) {
        JsonNode snippetJsonNode = resultNode.get(JSONNODE_ITEMS).get(FIRST_INDEX).get(JSONNODE_SNIPPET);
        String thumbnail = selectThumbnail(snippetJsonNode.get(JSONNODE_THUMBNAILS));

        List<ReviewBrief> reviewBriefList = lectureRepository.getReviewBriefList(resultNode.get(JSONNODE_ITEMS).get(FIRST_INDEX).get(JSONNODE_ID).asText(), DEFAULT_REVIEW_OFFSET, reviewLimit);

        String lectureCode = resultNode.get(JSONNODE_ITEMS).get(FIRST_INDEX).get(JSONNODE_ID).asText();
        String videoDuration = formatDuration(resultNode.get(JSONNODE_ITEMS).get(FIRST_INDEX).get(JSONNODE_CONTENT_DETAIL).get(JSONNODE_DURATION).asText());

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
                .playlist(FALSE)
                .enrolled(isEnrolled)
                .viewCount(resultNode.get(JSONNODE_ITEMS).get(FIRST_INDEX).get(JSONNODE_STATISTICS).get(JSONNODE_VIEW_COUNT).asInt())
                .publishedAt(snippetJsonNode.get(JSONNODE_PUBLISHED_AT).asText().substring(PUBLISHED_DATE_START_INDEX, PUBLISHED_DATE_END_INDEX))
                .thumbnail(thumbnail)
                .reviews(reviewBriefList)
                .reviewCount(reviewBriefList.size())
                .build();

        return videoDetail;
    }

    public PlaylistDetail buildPlaylistDetailResponse(JsonNode playlistNode, JsonNode indexNode, int reviewLimit, Set<String> enrolledPlaylistSet) {
        JsonNode snippetJsonNode = playlistNode.get(JSONNODE_ITEMS).get(FIRST_INDEX).get(JSONNODE_SNIPPET);
        String thumbnail = selectThumbnail(playlistNode.get(JSONNODE_ITEMS).get(FIRST_INDEX).get(JSONNODE_SNIPPET).get(JSONNODE_THUMBNAILS));

        IndexInfo indexInfo = buildIndexInfoResponse(indexNode);
        List<ReviewBrief> reviewBriefList = lectureRepository.getReviewBriefList(playlistNode.get(JSONNODE_ITEMS).get(FIRST_INDEX).get(JSONNODE_ID).asText(), DEFAULT_REVIEW_OFFSET, reviewLimit);

        String lectureCode = playlistNode.get(JSONNODE_ITEMS).get(FIRST_INDEX).get(JSONNODE_ID).asText();
        boolean isEnrolled = enrolledPlaylistSet.contains(lectureCode);
        String lectureTitle = snippetJsonNode.get(JSONNODE_TITLE).asText();
        String channel = snippetJsonNode.get(JSONNODE_CHANNEL_TITLE).asText();
        String description = snippetJsonNode.get(JSONNODE_DESCRIPTION).asText();

        return PlaylistDetail.builder()
                .lectureCode(lectureCode)
                .lectureTitle(unescapeHtml(lectureTitle))
                .channel(unescapeHtml(channel))
                .description(unescapeHtml(description))
                .playlist(TRUE)
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

        String totalDuration = formatDuration(Duration.ofSeconds(totalDurationSeconds.get()).toString());

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
            JsonNode videoNode = safeGet(youtubeUtil.getYoutubeResource(Video.builder().videoId(lectureCode).build()));

            if (snippetNode.get(JSONNODE_RESOURCE_ID).get(JSONNODE_KIND).asText().equals(JSONNODE_TYPE_VIDEO) && item.get(JSONNODE_STATUS).get(JSONNODE_PRIVACY_STATUS).asText().equals(JSONNODE_PUBLIC)) {
                String videoDuration = videoNode.get(JSONNODE_ITEMS).get(FIRST_INDEX).get(JSONNODE_CONTENT_DETAIL).get(JSONNODE_DURATION).asText();
                totalDurationSeconds.addAndGet(Duration.parse(videoDuration).getSeconds());
                String thumbnail = selectThumbnail(item.get(JSONNODE_SNIPPET).get(JSONNODE_THUMBNAILS));
                return Index.builder()
                        .index(snippetNode.get(JSONNODE_POSITION).asInt())
                        .lectureTitle(unescapeHtml(snippetNode.get(JSONNODE_TITLE).asText()))
                        .thumbnail(thumbnail)
                        .duration(formatDuration(videoDuration))
                        .build();
            }
            return null;
        });
    }


    public String selectThumbnail(JsonNode thumbnailsNode) {
        String selectedThumbnailUrl = "";

        JsonNode mediumThumbnailNode = thumbnailsNode.get(JSONNODE_THUMBNAIL_MEDIUM);
        if (mediumThumbnailNode != null) {
            selectedThumbnailUrl = mediumThumbnailNode.get(JSONNODE_THUMBNAIL_URL).asText();
        }

        JsonNode maxresThumbnailNode = thumbnailsNode.get(JSONNODE_THUMBNAIL_MAXRES);
        if (maxresThumbnailNode != null) {
            return maxresThumbnailNode.get(JSONNODE_THUMBNAIL_URL).asText();
        }

        return selectedThumbnailUrl;
    }

    public static String formatDuration(String durationString) {
        Duration duration = Duration.parse(durationString);

        long totalSeconds = duration.getSeconds();
        long hours = totalSeconds / SECONDS_IN_HOUR;
        long minutes = (totalSeconds % SECONDS_IN_HOUR) / SECONDS_IN_MINUTE;
        long seconds = totalSeconds % SECONDS_IN_MINUTE;

        if (hours > 0) {
            return String.format(FORMAT_WITH_HOUR, hours, minutes, seconds);
        } else {
            return String.format(FORMAT_WITHOUT_HOUR, minutes, seconds);
        }
    }

    public boolean checkIfPlaylist(String lectureCode) {
        String firstTwoCharacters = lectureCode.substring(LECTURE_CODE_START_INDEX, LECTURE_CODE_PLAYLIST_INDICATOR_LENGTH);
        return firstTwoCharacters.equals(PLAYLIST_CODE_INDICATOR);
    }

    public String unescapeHtml(String input) {
        return HtmlUtils.htmlUnescape(input);
    }

    public ResponseEntity<?> getLectureDetail(Long memberId, boolean isPlaylist, String
            lectureCode, LectureDetailParam lectureDetailParam) {
        lectureDetailParamValidate(isPlaylist, lectureDetailParam);

        if (lectureDetailParam.isIndexOnly()) {
            IndexInfo indexInfo = getPlaylistItems(lectureCode, lectureDetailParam.getIndexNextToken(), lectureDetailParam.getIndexLimit());
            return ResponseEntity.ok(indexInfo);
        }
        if (lectureDetailParam.isReviewOnly()) {
            List<ReviewBrief> reviewBriefList = getReviewBriefList(lectureCode, lectureDetailParam.getReviewOffset(), lectureDetailParam.getReviewLimit());
            return ResponseEntity.ok(reviewBriefList);
        }
        if (isPlaylist) {
            PlaylistDetail playlistDetail = getPlaylistDetail(memberId, lectureCode, lectureDetailParam.getIndexNextToken(), lectureDetailParam.getReviewLimit());
            return ResponseEntity.ok(playlistDetail);
        }
        VideoDetail videoDetail = getVideoDetail(memberId, lectureCode, lectureDetailParam.getReviewLimit());
        return ResponseEntity.ok(videoDetail);
    }

    public void lectureDetailParamValidate(boolean isPlaylist, LectureDetailParam lectureDetailParam) {
        if (lectureDetailParam.isIndexOnly() && lectureDetailParam.isReviewOnly()) {
            throw new TwoOnlyParamTrueException();
        }
        if (!isPlaylist && lectureDetailParam.isIndexOnly()) {
            throw new VideoIndexParamException();
        }
    }

    public <T> T safeGet(Future<T> future) {
        try {
            return future.get();
        } catch (Exception e) {
            log.error("error occurred. message = {}", e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }
}
