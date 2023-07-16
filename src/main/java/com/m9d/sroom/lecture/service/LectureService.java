package com.m9d.sroom.lecture.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.m9d.sroom.lecture.dto.response.Index;
import com.m9d.sroom.lecture.dto.response.Lecture;
import com.m9d.sroom.lecture.dto.response.ReviewBrief;
import com.m9d.sroom.lecture.dto.response.*;
import com.m9d.sroom.lecture.exception.LectureNotFoundException;
import com.m9d.sroom.lecture.exception.VideoNotFoundException;
import com.m9d.sroom.lecture.repository.LectureRepository;
import com.m9d.sroom.util.youtube.YoutubeUtil;
import com.m9d.sroom.util.youtube.resource.LectureList;
import com.m9d.sroom.util.youtube.resource.Playlist;
import com.m9d.sroom.util.youtube.resource.PlaylistItem;
import com.m9d.sroom.util.youtube.resource.Video;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class LectureService {

    private final LectureRepository lectureRepository;
    private final YoutubeUtil youtubeUtil;

    public KeywordSearch searchByKeyword(Long memberId, String keyword, int limit, String filter, String nextPageToken) throws Exception {
        LectureList lectureList = LectureList.builder()
                .keyword(keyword)
                .filter(filter)
                .limit(limit)
                .pageToken(nextPageToken)
                .build();
        JsonNode resultNode = youtubeUtil.getYoutubeResource(lectureList).get();
        Set<String> enrolledLectureSet = getLecturesByMemberId(memberId).get();

        KeywordSearch keywordSearch = buildLectureListResponse(resultNode, enrolledLectureSet);
        return keywordSearch;
    }

    public VideoDetail getVideoDetail(Long memberId, String lectureCode, int reviewLimit) throws Exception {
        Video video = Video.builder()
                .videoId(lectureCode)
                .build();
        JsonNode resultNode = youtubeUtil.getYoutubeResource(video).get();

        if (resultNode.get("pageInfo").get("totalResults").asInt() == 0) {
            throw new VideoNotFoundException();
        }

        Set<String> enrolledVideoSet = getEnrolledVideoByMemberId(memberId);

        VideoDetail videoDetail = buildVideoDetailResponse(resultNode, reviewLimit, enrolledVideoSet);
        return videoDetail;
    }

    public PlaylistDetail getPlaylistDetail(Long memberId, String lectureCode, String indexNextToken, int reviewLimit) throws Exception {
        Playlist playlist = Playlist.builder()
                .playlistId(lectureCode)
                .build();
        PlaylistItem playlistItem = PlaylistItem.builder()
                .playlistId(lectureCode)
                .nextPageToken(indexNextToken)
                .limit(50)
                .build();

        CompletableFuture<JsonNode> playlistFuture = youtubeUtil.getYoutubeResource(playlist);
        CompletableFuture<JsonNode> indexFuture = youtubeUtil.getYoutubeResource(playlistItem);
        CompletableFuture.allOf(playlistFuture, indexFuture).join();

        JsonNode playlistNode = playlistFuture.get();
        JsonNode indexNode = indexFuture.get();

        validateNodeIfNotFound(playlistNode);
        validateNodeIfNotFound(indexNode);

        Set<String> enrolledPlaylistSet = getEnrolledPlaylistByMemberId(memberId);

        PlaylistDetail playlistDetail = buildPlaylistDetailResponse(playlistNode, indexNode, reviewLimit, enrolledPlaylistSet);
        return playlistDetail;
    }

    public void validateNodeIfNotFound(JsonNode node) {
        if (node.get("pageInfo").get("totalResults").asInt() == 0) {
            throw new LectureNotFoundException();
        }
    }


    public IndexInfo getPlaylistItems(String lectureCode, String indexNextToken, int indexLimit) throws Exception {
        PlaylistItem playlistItem = PlaylistItem.builder()
                .playlistId(lectureCode)
                .nextPageToken(indexNextToken)
                .limit(indexLimit)
                .build();
        JsonNode resultNode = youtubeUtil.getYoutubeResource(playlistItem).get();

        validateNodeIfNotFound(resultNode);

        IndexInfo indexInfo = buildIndexInfoResponse(resultNode).get();
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
        int count = 1;
        try {
            for (JsonNode item : resultNode.get("items")) {
                JsonNode snippetNode = item.get("snippet");
                boolean isPlaylist = item.get("id").get("kind").asText().equals("youtube#playlist");
                String thumbnail = selectThumbnail(snippetNode.get("thumbnails"));

                String lectureCode;
                if (isPlaylist) {
                    lectureCode = item.get("id").get("playlistId").asText();
                } else {
                    lectureCode = item.get("id").get("videoId").asText();
                }
                boolean isEnrolled = enrolledLectureSet.contains(lectureCode);
                String lectureTitle = snippetNode.get("title").asText();
                String channel = snippetNode.get("channelTitle").asText();
                String description = snippetNode.get("description").asText();

                Lecture lecture = Lecture.builder()
                        .lectureTitle(unescapeHtml(lectureTitle))
                        .description(unescapeHtml(description))
                        .channel(unescapeHtml(channel))
                        .lectureCode(lectureCode)
                        .isEnrolled(isEnrolled)
                        .isPlaylist(isPlaylist)
                        .thumbnail(thumbnail)
                        .build();
                lectureList.add(lecture);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        String nextPageToken = resultNode.has("nextPageToken") ? resultNode.get("nextPageToken").asText() : null;
        String prevPageToken = resultNode.has("prevPageToken") ? resultNode.get("prevPageToken").asText() : null;
        KeywordSearch keywordSearch = KeywordSearch.builder()
                .nextPageToken(nextPageToken)
                .prevPageToken(prevPageToken)
                .resultPerPage(resultNode.get("pageInfo").get("resultsPerPage").asInt())
                .lectures(lectureList)
                .build();
        return keywordSearch;
    }

    public VideoDetail buildVideoDetailResponse(JsonNode resultNode, int reviewLimit, Set<String> enrolledVideoSet) {
        try {
            JsonNode snippetJsonNode = resultNode.get("items").get(0).get("snippet");
            String thumbnail = selectThumbnail(snippetJsonNode.get("thumbnails"));

            List<ReviewBrief> reviewBriefList = lectureRepository.getReviewBriefList(resultNode.get("items").get(0).get("id").asText(), 0, reviewLimit);

            String lectureCode = resultNode.get("items").get(0).get("id").asText();
            String videoDuration = formatDuration(resultNode.get("items").get(0).get("contentDetails").get("duration").asText());

            boolean isEnrolled = enrolledVideoSet.contains(lectureCode);
            String lectureTitle = snippetJsonNode.get("title").asText();
            String channel = snippetJsonNode.get("channelTitle").asText();
            String description = snippetJsonNode.get("description").asText();

            VideoDetail videoDetail = VideoDetail.builder()
                    .lectureCode(lectureCode)
                    .lectureTitle(unescapeHtml(lectureTitle))
                    .channel(unescapeHtml(channel))
                    .description(unescapeHtml(description))
                    .duration(videoDuration)
                    .isPlaylist(false)
                    .isEnrolled(isEnrolled)
                    .viewCount(resultNode.get("items").get(0).get("statistics").get("viewCount").asInt())
                    .publishedAt(snippetJsonNode.get("publishedAt").asText().substring(0, 10))
                    .thumbnail(thumbnail)
                    .reviews(reviewBriefList)
                    .reviewCount(reviewBriefList.size())
                    .build();

            return videoDetail;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public PlaylistDetail buildPlaylistDetailResponse(JsonNode playlistNode, JsonNode indexNode, int reviewLimit, Set<String> enrolledPlaylistSet) {
        try {
            JsonNode snippetJsonNode = playlistNode.get("items").get(0).get("snippet");
            String thumbnail = selectThumbnail(playlistNode.get("items").get(0).get("snippet").get("thumbnails"));

            IndexInfo indexInfo = buildIndexInfoResponse(indexNode).get();
            List<ReviewBrief> reviewBriefList = lectureRepository.getReviewBriefList(playlistNode.get("items").get(0).get("id").asText(), 0, reviewLimit);

            String lectureCode = playlistNode.get("items").get(0).get("id").asText();
            boolean isEnrolled = enrolledPlaylistSet.contains(lectureCode);
            String lectureTitle = snippetJsonNode.get("title").asText();
            String channel = snippetJsonNode.get("channelTitle").asText();
            String description = snippetJsonNode.get("description").asText();

            PlaylistDetail playlistDetail = PlaylistDetail.builder()
                    .lectureCode(lectureCode)
                    .lectureTitle(unescapeHtml(lectureTitle))
                    .channel(unescapeHtml(channel))
                    .description(unescapeHtml(description))
                    .isPlaylist(true)
                    .isEnrolled(isEnrolled)
                    .lectureCount(playlistNode.get("items").get(0).get("contentDetails").get("itemCount").asInt())
                    .thumbnail(thumbnail)
                    .indexInfo(indexInfo)
                    .reviews(reviewBriefList)
                    .build();
            return playlistDetail;
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    public CompletableFuture<IndexInfo> buildIndexInfoResponse(JsonNode indexNode) {
        try {
            String nextPageToken = indexNode.has("nextPageToken") ? indexNode.get("nextPageToken").asText() : null;

            AtomicLong totalDurationSeconds = new AtomicLong();

            List<CompletableFuture<Index>> futuresList = new ArrayList<>();
            for (JsonNode item : indexNode.get("items")) {
                JsonNode snippetNode = item.get("snippet");

                String lectureCode = snippetNode.get("resourceId").get("videoId").asText();

                CompletableFuture<Index> indexFuture = CompletableFuture.supplyAsync(() -> {
                    JsonNode videoNode;
                    try {
                        videoNode = youtubeUtil.getYoutubeResource(Video.builder()
                                        .videoId(lectureCode)
                                        .build())
                                .get();
                    } catch (Exception e) {
                        throw new LectureNotFoundException();
                    }

                    if (snippetNode.get("resourceId").get("kind").asText().equals("youtube#video") && item.get("status").get("privacyStatus").asText().equals("public")) {
                        String videoDuration = videoNode.get("items").get(0).get("contentDetails").get("duration").asText();
                        totalDurationSeconds.addAndGet(Duration.parse(videoDuration).getSeconds());
                        String thumbnail = selectThumbnail(item.get("snippet").get("thumbnails"));
                        Index index = Index.builder()
                                .index(snippetNode.get("position").asInt())
                                .lectureTitle(unescapeHtml(snippetNode.get("title").asText()))
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
            throw new RuntimeException(e);
        }
    }


    public String selectThumbnail(JsonNode thumbnailsNode) {
        String selectedThumbnailUrl = "";

        JsonNode mediumThumbnailNode = thumbnailsNode.get("medium");
        if (mediumThumbnailNode != null) {
            selectedThumbnailUrl = mediumThumbnailNode.get("url").asText();
        }

        JsonNode maxresThumbnailNode = thumbnailsNode.get("maxres");
        if (maxresThumbnailNode != null) {
            return maxresThumbnailNode.get("url").asText();
        }

        return selectedThumbnailUrl;
    }

    public static String formatDuration(String durationString) {
        Duration duration = Duration.parse(durationString);

        long totalSeconds = duration.getSeconds();
        long hours = totalSeconds / 3600;
        long minutes = (totalSeconds % 3600) / 60;
        long seconds = totalSeconds % 60;

        if (hours > 0) {
            return String.format("%d:%02d:%02d", hours, minutes, seconds);
        } else {
            return String.format("%d:%02d", minutes, seconds);
        }
    }

    public boolean checkIfPlaylist(String lectureCode) {
        String firstTwoCharacters = lectureCode.substring(0, 2);
        return firstTwoCharacters.equals("PL");
    }

    public String unescapeHtml(String input) {
        return HtmlUtils.htmlUnescape(input);
    }
}
