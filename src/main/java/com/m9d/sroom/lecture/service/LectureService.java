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
import org.springframework.stereotype.Service;

import java.security.InvalidParameterException;
import java.util.*;

@RequiredArgsConstructor
@Service
public class LectureService {

    private final YoutubeService youtubeService;
    private final LectureRepository lectureRepository;

    public KeywordSearch searchByKeyword(Long memberId, String keyword, int limit, String nextPageToken, String prevPageToken) throws Exception {

        JsonNode resultNode = youtubeService.getLectureListFromYoutube(keyword, limit, nextPageToken, prevPageToken);

        Set<String> enrolledLectureSet = getLecturesByMemberId(memberId);
        KeywordSearch keywordSearch = buildLectureListResponse(resultNode, enrolledLectureSet);

        return keywordSearch;
    }

    public VideoDetail getVideoDetail(Long memberId, String lectureCode, int reviewLimit) throws Exception {

        JsonNode resultNode = youtubeService.getVideoDetailFromYoutube(lectureCode);

        if (resultNode.get("pageInfo").get("totalResults").asInt() == 0) {
            throw new VideoNotFoundException();
        }
        Set<String> enrolledLectureSet = getLecturesByMemberId(memberId);

        VideoDetail videoDetail = buildVideoDetailResponse(resultNode, reviewLimit, enrolledLectureSet);
        return videoDetail;
    }

    public PlaylistDetail getPlaylistDetail(Long memberId, String lectureCode, String indexNextToken, int indexLimit, int reviewLimit) throws Exception {

        JsonNode playlistNode = youtubeService.getPlaylistDetailFromYoutube(lectureCode);

        if (playlistNode.get("pageInfo").get("totalResults").asInt() == 0) {
            throw new PlaylistNotFoundException();
        }

        JsonNode indexNode = youtubeService.getPlaylistItemsFromYoutube(lectureCode, indexNextToken, indexLimit);

        if (indexNode.get("pageInfo").get("totalResults").asInt() == 0) {
            throw new PlaylistItemNotFoundException();
        }

        Set<String> enrolledLectureSet = getLecturesByMemberId(memberId);

        PlaylistDetail playlistDetail = buildPlaylistDetailResponse(playlistNode, indexNode, reviewLimit, enrolledLectureSet);
        return playlistDetail;
    }

    public IndexInfo getPlaylistItems(Long memberId, String lectureCode, String indexNextToken, int indexLimit) throws Exception {

        JsonNode indexNode = youtubeService.getPlaylistItemsFromYoutube(lectureCode, indexNextToken, indexLimit);

        if (indexNode.get("pageInfo").get("totalResults").asInt() == 0) {
            throw new PlaylistItemNotFoundException();
        }
        Set<String> enrolledLectureSet = getLecturesByMemberId(memberId);

        IndexInfo indexInfo = buildIndexInfoResponse(indexNode, enrolledLectureSet);
        return indexInfo;
    }

    public List<ReviewBrief> getReviewBriefList(String lectureCode, int reviewOffset, int reviewLimit) {
        List<ReviewBrief> reviewBriefList = lectureRepository.getReviewBriefList(lectureCode, reviewOffset, reviewLimit);
        return reviewBriefList;
    }

    public Set<String> getLecturesByMemberId(Long memberId) {
        Set<String> lectureSet = new HashSet<>();
        lectureSet.addAll(lectureRepository.getVideosByMemberId(memberId));
        lectureSet.addAll(lectureRepository.getPlaylistByMemberId(memberId));

        return lectureSet;
    }

    public KeywordSearch buildLectureListResponse(JsonNode resultNode, Set<String> enrolledLectureSet) {
        List<Lecture> lectureList = new ArrayList<>();
        try {
            for (JsonNode item : resultNode.get("items")) {
                JsonNode snippetNode = item.get("snippet");
                boolean isPlaylist = item.get("id").get("kind").asText().equals("youtube#playlist");
                String thumbnailWithHighestWidth = selectThumbnailWithHighestWidth(snippetNode.get("thumbnails"));

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
                        .thumbnail(thumbnailWithHighestWidth)
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
            String thumbnailWithHighestWidth = selectThumbnailWithHighestWidth(snippetJsonNode.get("thumbnails"));

            List<ReviewBrief> reviewBriefList = lectureRepository.getReviewBriefList(resultNode.get("items").get(0).get("id").asText(), 0, reviewLimit);

            String lectureCode = resultNode.get("items").get(0).get("id").asText();
            boolean isEnrolled = enrolledLectureSet.contains(lectureCode);

            VideoDetail videoDetail = VideoDetail.builder()
                    .lectureCode(lectureCode)
                    .lectureTitle(snippetJsonNode.get("title").asText())
                    .channel(snippetJsonNode.get("channelTitle").asText())
                    .description(snippetJsonNode.get("description").asText())
                    .isPlaylist(false)
                    .isEnrolled(isEnrolled)
                    .viewCount(resultNode.get("items").get(0).get("statistics").get("viewCount").asInt())
                    .publishedAt(snippetJsonNode.get("publishedAt").asText().substring(0, 10))
                    .thumbnail(thumbnailWithHighestWidth)
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
            String thumbnailWithHighestWidth = selectThumbnailWithHighestWidth(playlistNode.get("items").get(0).get("snippet").get("thumbnails"));

            IndexInfo indexInfo = buildIndexInfoResponse(indexNode, enrolledLectureSet);
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
                    .thumbnail(thumbnailWithHighestWidth)
                    .indexInfo(indexInfo)
                    .reviews(reviewBriefList)
                    .build();
            return playlistDetail;
        } catch (Exception e) {
            throw new InvalidParameterException();
        }
    }

    public IndexInfo buildIndexInfoResponse(JsonNode indexNode, Set<String> enrolledLectureSet) {

        try {
            String nextPageToken = indexNode.has("nextPageToken") ? indexNode.get("nextPageToken").asText() : null;

            List<Index> indexList = new ArrayList<>();
            for (JsonNode item : indexNode.get("items")) {
                JsonNode snippetNode = item.get("snippet");

                String lectureCode = snippetNode.get("resourceId").get("videoId").asText();
                boolean isEnrolled = enrolledLectureSet.contains(lectureCode);

                if (snippetNode.get("resourceId").get("kind").asText().equals("youtube#video") && item.get("status").get("privacyStatus").asText().equals("public")) {
                    String itemThumbnailWithHighestWidth = selectThumbnailWithHighestWidth(item.get("snippet").get("thumbnails"));
                    Index index = Index.builder()
                            .index(snippetNode.get("position").asInt())
                            .lectureCode(lectureCode)
                            .isEnrolled(isEnrolled)
                            .lectureTitle(snippetNode.get("title").asText())
                            .thumbnail(itemThumbnailWithHighestWidth)
                            .build();
                    indexList.add(index);
                }
            }
            IndexInfo indexInfoResult = IndexInfo.builder()
                    .indexList(indexList)
                    .nextPageToken(nextPageToken)
                    .build();

            return indexInfoResult;
        } catch (Exception e) {
            throw new InvalidParameterException();
        }
    }

    public static String selectThumbnailWithHighestWidth(JsonNode thumbnailsNode) {
        int maxWidth = 0;
        String selectedThumbnailUrl = "";

        for (JsonNode thumbnailNode : thumbnailsNode) {
            int width = thumbnailNode.get("width").asInt();
            String url = thumbnailNode.get("url").asText();

            if (width > maxWidth) {
                maxWidth = width;
                selectedThumbnailUrl = url;
            }
        }

        return selectedThumbnailUrl;
    }
}
