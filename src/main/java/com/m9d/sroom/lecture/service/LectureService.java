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
        try {
            JsonNode resultNode = youtubeUtil.getYoutubeResource(lectureList).get();
            Set<String> enrolledLectureSet = getLecturesByMemberId(memberId).get();
            KeywordSearch keywordSearch = buildLectureListResponse(resultNode, enrolledLectureSet);
            return keywordSearch;
        } catch (Exception e) {
            log.info("error occurred. message = {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public VideoDetail getVideoDetail(Long memberId, String lectureCode, int reviewLimit) {
        Video video = Video.builder()
                .videoId(lectureCode)
                .build();
        try {
            JsonNode resultNode = youtubeUtil.getYoutubeResource(video).get();

            if (resultNode.get("pageInfo").get("totalResults").asInt() == 0) {
                throw new VideoNotFoundException();
            }

            Set<String> enrolledVideoSet = getEnrolledVideoByMemberId(memberId);

            VideoDetail videoDetail = buildVideoDetailResponse(resultNode, reviewLimit, enrolledVideoSet);
            return videoDetail;
        } catch (Exception e) {
            log.info("error occurred. message = {}", e.getMessage());
            throw new RuntimeException(e);
        }
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

        try {
            JsonNode playlistNode = playlistFuture.get();
            JsonNode indexNode = indexFuture.get();

            validateNodeIfNotFound(playlistNode);
            validateNodeIfNotFound(indexNode);

            Set<String> enrolledPlaylistSet = getEnrolledPlaylistByMemberId(memberId);

            PlaylistDetail playlistDetail = buildPlaylistDetailResponse(playlistNode, indexNode, reviewLimit, enrolledPlaylistSet);
            return playlistDetail;
        } catch (Exception e) {
            log.info("error occurred. message = {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public void validateNodeIfNotFound(JsonNode node) {
        if (node.get("pageInfo").get("totalResults").asInt() == 0) {
            throw new LectureNotFoundException();
        }
    }


    public IndexInfo getPlaylistItems(String lectureCode, String indexNextToken, int indexLimit) {
        PlaylistItem playlistItem = PlaylistItem.builder()
                .playlistId(lectureCode)
                .nextPageToken(indexNextToken)
                .limit(indexLimit)
                .build();
        try {
            JsonNode resultNode = youtubeUtil.getYoutubeResource(playlistItem).get();
            validateNodeIfNotFound(resultNode);

            IndexInfo indexInfo = buildIndexInfoResponse(resultNode).get();
            return indexInfo;
        } catch (Exception e) {
            log.info("error occurred. message = {}", e.getMessage());
            throw new RuntimeException(e);
        }
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
        try {
            for (JsonNode item : resultNode.get(JSONNODE_ITEMS)) {
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
        } catch (Exception e) {
            log.info("error occurred. message = {}", e.getMessage());
            throw new RuntimeException(e);
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
        try {
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
        } catch (Exception e) {
            log.info("error occurred. message = {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public PlaylistDetail buildPlaylistDetailResponse(JsonNode playlistNode, JsonNode indexNode, int reviewLimit, Set<String> enrolledPlaylistSet) {
        try {
            JsonNode snippetJsonNode = playlistNode.get(JSONNODE_ITEMS).get(FIRST_INDEX).get(JSONNODE_SNIPPET);
            String thumbnail = selectThumbnail(playlistNode.get(JSONNODE_ITEMS).get(FIRST_INDEX).get(JSONNODE_SNIPPET).get(JSONNODE_THUMBNAILS));

            IndexInfo indexInfo = buildIndexInfoResponse(indexNode).get();
            List<ReviewBrief> reviewBriefList = lectureRepository.getReviewBriefList(playlistNode.get(JSONNODE_ITEMS).get(FIRST_INDEX).get(JSONNODE_ID).asText(), DEFAULT_REVIEW_OFFSET, reviewLimit);

            String lectureCode = playlistNode.get(JSONNODE_ITEMS).get(FIRST_INDEX).get(JSONNODE_ID).asText();
            boolean isEnrolled = enrolledPlaylistSet.contains(lectureCode);
            String lectureTitle = snippetJsonNode.get(JSONNODE_TITLE).asText();
            String channel = snippetJsonNode.get(JSONNODE_CHANNEL_TITLE).asText();
            String description = snippetJsonNode.get(JSONNODE_DESCRIPTION).asText();

            PlaylistDetail playlistDetail = PlaylistDetail.builder()
                    .lectureCode(lectureCode)
                    .lectureTitle(unescapeHtml(lectureTitle))
                    .channel(unescapeHtml(channel))
                    .description(unescapeHtml(description))
                    .playlist(TRUE)
                    .enrolled(isEnrolled)
                    .lectureCount(playlistNode.get(JSONNODE_PLAYLIST_ID).get(FIRST_INDEX).get(JSONNODE_CONTENT_DETAIL).get(JSONNODE_ITEM_COUNT).asInt())
                    .thumbnail(thumbnail)
                    .indexes(indexInfo)
                    .reviews(reviewBriefList)
                    .build();
            return playlistDetail;
        } catch (Exception e) {
            log.info("error occurred. message = {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public CompletableFuture<IndexInfo> buildIndexInfoResponse(JsonNode indexNode) {
        try {
            String nextPageToken = indexNode.has(JSONNODE_NEXT_PAGE_TOKEN) ? indexNode.get(JSONNODE_NEXT_PAGE_TOKEN).asText() : null;

            AtomicLong totalDurationSeconds = new AtomicLong();

            List<CompletableFuture<Index>> futuresList = new ArrayList<>();
            for (JsonNode item : indexNode.get(JSONNODE_ITEMS)) {
                JsonNode snippetNode = item.get(JSONNODE_SNIPPET);

                String lectureCode = snippetNode.get(JSONNODE_RESOURCE_ID).get(JSONNODE_VIDEO_ID).asText();

                CompletableFuture<Index> indexFuture = CompletableFuture.supplyAsync(() -> {
                    JsonNode videoNode;
                    try {
                        videoNode = youtubeUtil.getYoutubeResource(Video.builder()
                                        .videoId(lectureCode)
                                        .build())
                                .get();
                    } catch (Exception e) {
                        log.info("error occurred. message = {}", e.getMessage());
                        throw new LectureNotFoundException();
                    }

                    if (snippetNode.get(JSONNODE_RESOURCE_ID).get(JSONNODE_KIND).asText().equals(JSONNODE_TYPE_VIDEO) && item.get(JSONNODE_STATUS).get(JSONNODE_PRIVACY_STATUS).asText().equals(JSONNODE_PUBLIC)) {
                        String videoDuration = videoNode.get(JSONNODE_ITEMS).get(FIRST_INDEX).get(JSONNODE_CONTENT_DETAIL).get(JSONNODE_DURATION).asText();
                        totalDurationSeconds.addAndGet(Duration.parse(videoDuration).getSeconds());
                        String thumbnail = selectThumbnail(item.get(JSONNODE_SNIPPET).get(JSONNODE_THUMBNAILS));
                        Index index = Index.builder()
                                .index(snippetNode.get(JSONNODE_POSITION).asInt())
                                .lectureTitle(unescapeHtml(snippetNode.get(JSONNODE_TITLE).asText()))
                                .thumbnail(thumbnail)
                                .duration(formatDuration(videoDuration))
                                .build();
                        return index;
                    }
                    return null;
                });

                futuresList.add(indexFuture);
            }

            CompletableFuture<Void> allFutures = CompletableFuture.allOf(futuresList.toArray(new CompletableFuture[futuresList.size()]));

            CompletableFuture<IndexInfo> indexInfoFuture = allFutures.thenApply(v -> {
                List<Index> indexList = futuresList.stream()
                        .map(CompletableFuture::join)
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList());

                String totalDuration = formatDuration(Duration.ofSeconds(totalDurationSeconds.get()).toString());

                IndexInfo indexInfoResult = IndexInfo.builder()
                        .indexList(indexList)
                        .totalDuration(totalDuration)
                        .nextPageToken(nextPageToken)
                        .build();
                return indexInfoResult;
            });

            return indexInfoFuture;

        } catch (Exception e) {
            log.info("error occurred. message = {}", e.getMessage());
            throw new RuntimeException(e);
        }
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

    public ResponseEntity<?> getLectureDetail(Long memberId, boolean isPlaylist, String lectureCode, LectureDetailParam lectureDetailParam) {
        lectureDetailParamValidate(isPlaylist, lectureDetailParam);
        log.info("param = {}, {}, {}", lectureDetailParam.isIndexOnly(), lectureDetailParam.isReviewOnly(), lectureDetailParam.getReviewLimit());

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
}
