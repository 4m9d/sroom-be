package com.m9d.sroom.lecture.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.m9d.sroom.lecture.dto.response.Index;
import com.m9d.sroom.lecture.dto.response.Lecture;
import com.m9d.sroom.lecture.dto.response.ReviewBrief;
import com.m9d.sroom.lecture.dto.response.*;
import com.m9d.sroom.lecture.exception.PlaylistItemNotFoundException;
import com.m9d.sroom.lecture.exception.PlaylistNotFoundException;
import com.m9d.sroom.lecture.exception.VideoNotFoundException;
import com.m9d.sroom.lecture.repository.LectureRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.security.InvalidParameterException;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class LectureService {

    private final YoutubeService youtubeService;
    private final LectureRepository lectureRepository;

    public KeywordSearch searchByKeyword(Long memberId, String keyword, int limit, String nextPageToken, String prevPageToken) throws Exception {

        JsonNode resultNode = youtubeService.getLectureListFromYoutube(keyword, limit, nextPageToken, prevPageToken);

        Set<String> enrolledLectureSet = getLecturesByMemberId(memberId).get();
        KeywordSearch keywordSearch = buildLectureListResponse(resultNode, enrolledLectureSet);

        return keywordSearch;
    }

    public VideoDetail getVideoDetail(Long memberId, String lectureCode, int reviewLimit) throws Exception {

        JsonNode resultNode = youtubeService.getVideoDetailFromYoutube(lectureCode);

        if (resultNode.get("pageInfo").get("totalResults").asInt() == 0) {
            throw new VideoNotFoundException();
        }
        Set<String> enrolledLectureSet = getLecturesByMemberId(memberId).get();

        VideoDetail videoDetail = buildVideoDetailResponse(resultNode, reviewLimit, enrolledLectureSet);
        return videoDetail;
    }

    public PlaylistDetail getPlaylistDetail(Long memberId, String lectureCode, String indexNextToken, int indexLimit, int reviewLimit) throws Exception {

        CompletableFuture<JsonNode> playlistFuture = youtubeService.getPlaylistDetailFromYoutube(lectureCode);
        CompletableFuture<JsonNode> indexFuture = youtubeService.getPlaylistItemsFromYoutube(lectureCode, indexNextToken, indexLimit);
        CompletableFuture<Set<String>> enrolledLectureFuture = getLecturesByMemberId(memberId);

        CompletableFuture.allOf(playlistFuture, indexFuture, enrolledLectureFuture).join();

        JsonNode playlistNode = playlistFuture.get();

        if (playlistNode.get("pageInfo").get("totalResults").asInt() == 0) {
            throw new PlaylistNotFoundException();
        }

        JsonNode indexNode = indexFuture.get();

        if (indexNode.get("pageInfo").get("totalResults").asInt() == 0) {
            throw new PlaylistItemNotFoundException();
        }


        Set<String> enrolledLectureSet = enrolledLectureFuture.get();

        PlaylistDetail playlistDetail = buildPlaylistDetailResponse(playlistNode, indexNode, reviewLimit, enrolledLectureSet);
        return playlistDetail;
    }

    public IndexInfo getPlaylistItems(Long memberId, String lectureCode, String indexNextToken, int indexLimit) throws Exception {

        JsonNode indexNode = youtubeService.getPlaylistItemsFromYoutube(lectureCode, indexNextToken, indexLimit).get();

        if (indexNode.get("pageInfo").get("totalResults").asInt() == 0) {
            throw new PlaylistItemNotFoundException();
        }
        Set<String> enrolledLectureSet = getLecturesByMemberId(memberId).get();

        IndexInfo indexInfo = buildIndexInfoResponse(indexNode, enrolledLectureSet).get();
        return indexInfo;
    }

    public List<ReviewBrief> getReviewBriefList(String lectureCode, int reviewOffset, int reviewLimit) {
        List<ReviewBrief> reviewBriefList = lectureRepository.getReviewBriefList(lectureCode, reviewOffset, reviewLimit);
        return reviewBriefList;
    }

    @Async
    public CompletableFuture<Set<String>> getLecturesByMemberId(Long memberId) {
        Set<String> lectureSet = new HashSet<>();
        lectureSet.addAll(lectureRepository.getVideosByMemberId(memberId));
        lectureSet.addAll(lectureRepository.getPlaylistByMemberId(memberId));
        return CompletableFuture.completedFuture(lectureSet);
    }

    public KeywordSearch buildLectureListResponse(JsonNode resultNode, Set<String> enrolledLectureSet) {
        List<Lecture> lectureList = new ArrayList<>();
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

                Lecture lecture = Lecture.builder()
                        .lectureTitle(snippetNode.get("title").asText())
                        .description(snippetNode.get("description").asText())
                        .channel(snippetNode.get("channelTitle").asText())
                        .lectureCode(lectureCode)
                        .isEnrolled(isEnrolled)
                        .isPlaylist(isPlaylist)
                        .thumbnail(thumbnail)
                        .build();
                lectureList.add(lecture);
            }
        } catch (Exception e) {
            throw new InvalidParameterException();
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

    public VideoDetail buildVideoDetailResponse(JsonNode resultNode, int reviewLimit, Set<String> enrolledLectureSet) {

        try {
            JsonNode snippetJsonNode = resultNode.get("items").get(0).get("snippet");
            String thumbnail = selectThumbnail(snippetJsonNode.get("thumbnails"));

            List<ReviewBrief> reviewBriefList = lectureRepository.getReviewBriefList(resultNode.get("items").get(0).get("id").asText(), 0, reviewLimit);

            String lectureCode = resultNode.get("items").get(0).get("id").asText();
            boolean isEnrolled = enrolledLectureSet.contains(lectureCode);
            String videoDuration = formatDuration(resultNode.get("items").get(0).get("contentDetails").get("duration").asText());

            VideoDetail videoDetail = VideoDetail.builder()
                    .lectureCode(lectureCode)
                    .lectureTitle(snippetJsonNode.get("title").asText())
                    .channel(snippetJsonNode.get("channelTitle").asText())
                    .description(snippetJsonNode.get("description").asText())
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
            throw new InvalidParameterException();
        }
    }

    public PlaylistDetail buildPlaylistDetailResponse(JsonNode playlistNode, JsonNode indexNode, int reviewLimit, Set<String> enrolledLectureSet) {
        try {
            JsonNode snippetJsonNode = playlistNode.get("items").get(0).get("snippet");
            String thumbnail = selectThumbnail(playlistNode.get("items").get(0).get("snippet").get("thumbnails"));

            IndexInfo indexInfo = buildIndexInfoResponse(indexNode, enrolledLectureSet).get();
            List<ReviewBrief> reviewBriefList = lectureRepository.getReviewBriefList(playlistNode.get("items").get(0).get("id").asText(), 0, reviewLimit);

            String lectureCode = playlistNode.get("items").get(0).get("id").asText();
            boolean isEnrolled = enrolledLectureSet.contains(lectureCode);

            PlaylistDetail playlistDetail = PlaylistDetail.builder()
                    .lectureCode(lectureCode)
                    .lectureTitle(snippetJsonNode.get("title").asText())
                    .channel(snippetJsonNode.get("channelTitle").asText())
                    .description(snippetJsonNode.get("description").asText())
                    .isPlaylist(true)
                    .isEnrolled(isEnrolled)
                    .lectureCount(playlistNode.get("items").get(0).get("contentDetails").get("itemCount").asInt())
                    .thumbnail(thumbnail)
                    .indexInfo(indexInfo)
                    .reviews(reviewBriefList)
                    .build();
            return playlistDetail;
        } catch (Exception e) {
            throw new InvalidParameterException();
        }
    }

    public CompletableFuture<IndexInfo> buildIndexInfoResponse(JsonNode indexNode, Set<String> enrolledLectureSet) {

        try {
            String nextPageToken = indexNode.has("nextPageToken") ? indexNode.get("nextPageToken").asText() : null;

            List<CompletableFuture<Index>> futuresList = new ArrayList<>();
            for (JsonNode item : indexNode.get("items")) {
                JsonNode snippetNode = item.get("snippet");

                String lectureCode = snippetNode.get("resourceId").get("videoId").asText();

                CompletableFuture<Index> indexFuture = CompletableFuture.supplyAsync(() -> {
                    JsonNode videoNode;
                    try {
                        videoNode = youtubeService.getVideoDetailFromYoutube(lectureCode);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                    boolean isEnrolled = enrolledLectureSet.contains(lectureCode);

                    if (snippetNode.get("resourceId").get("kind").asText().equals("youtube#video") && item.get("status").get("privacyStatus").asText().equals("public")) {
                        String videoDuration = formatDuration(videoNode.get("items").get(0).get("contentDetails").get("duration").asText());
                        String thumbnail = selectThumbnail(item.get("snippet").get("thumbnails"));
                        Index index = Index.builder()
                                .index(snippetNode.get("position").asInt())
                                .isEnrolled(isEnrolled)
                                .lectureTitle(snippetNode.get("title").asText())
                                .thumbnail(thumbnail)
                                .duration(videoDuration)
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
                IndexInfo indexInfoResult = IndexInfo.builder()
                        .indexList(indexList)
                        .nextPageToken(nextPageToken)
                        .build();
                return indexInfoResult;
            });

            return indexInfoFuture;

        } catch (Exception e) {
            throw new InvalidParameterException();
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
            return String.format("%02d:%02d:%02d", hours, minutes, seconds);
        } else if (minutes > 0) {
            return String.format("%02d:%02d", minutes, seconds);
        } else {
            return String.format("%02d", seconds);
        }
    }

}
