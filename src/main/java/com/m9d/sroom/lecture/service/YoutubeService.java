package com.m9d.sroom.lecture.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.m9d.sroom.config.secret.Secret;
import com.m9d.sroom.lecture.dto.response.KeywordSearchRes;
import com.m9d.sroom.lecture.dto.response.Lecture;
import com.m9d.sroom.lecture.dto.response.PlaylistDetail;
import com.m9d.sroom.lecture.dto.response.VideoDetail;
import com.m9d.sroom.lecture.exception.PlaylistNotFoundException;
import com.m9d.sroom.lecture.exception.VideoNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
@Slf4j
public class YoutubeService {

    public KeywordSearchRes searchByKeyword(String keyword, int limit, String nextPageToken, String prevPageToken) throws Exception {

        String url = createSearchURI(keyword, limit, nextPageToken, prevPageToken);
        JsonNode resultNode = requestToYoutube(url);

        KeywordSearchRes keywordSearchRes = buildLectureListResponse(resultNode);
        return keywordSearchRes;
    }

    public VideoDetail getVideoDetail(String lectureId, int indexLimit, int reviewLimit) throws Exception {

        String url = createVideoDetailURl(lectureId);
        JsonNode resultNode = requestToYoutube(url);

        if (resultNode.get("pageInfo").get("totalResults").asInt() == 0) {
            throw new VideoNotFoundException();
        }

        VideoDetail videoDetail = buildVideoDetailResponse(resultNode, indexLimit, reviewLimit);
        return videoDetail;
    }

    public PlaylistDetail getPlaylistDetail(String lectureId) throws Exception {

        String url = createPlaylistDetailUrl(lectureId);
        System.out.println(url);
        JsonNode resultNode = requestToYoutube(url);

        System.out.println(resultNode.toPrettyString());

        if (resultNode.get("pageInfo").get("totalResults").asInt() == 0) {
            throw new PlaylistNotFoundException();
        }

        PlaylistDetail playlistDetail = buildPlaylistDetailResponse(resultNode);
        return playlistDetail;
    }

    public String createSearchURI(String keyword, int limit, String nextPageToken, String prevPageToken) {
        String url = "https://www.googleapis.com/youtube/v3/search?";
        String pageTokenOrNull = chooseTokenOrNull(nextPageToken, prevPageToken);

        String partQuery = "part=id,snippet";
        String fieldsQuery = "&fields=nextPageToken,prevPageToken,pageInfo,items(id,snippet(title,channelTitle,thumbnails,description,publishTime))";
        String maxResultsQuery = "&maxResults=".concat(String.valueOf(limit));
        String apikeyQuery = "&key=".concat(Secret.getGoogleCloudApiKey());
        String typeQuery = "&type=playlist,video";
        String qQuery = "&q=".concat(keyword);

        url = url.concat(partQuery).concat(fieldsQuery).concat(maxResultsQuery).concat(typeQuery).concat(apikeyQuery).concat(qQuery);
        if (pageTokenOrNull != null) {
            String pageTokenQuery = "&pageToken=".concat(pageTokenOrNull);
            url = url.concat(pageTokenQuery);
        }
        log.info("youtube keyword search request uri:" + url);
        return url;
    }

    private String createVideoDetailURl(String lectureId) {
        String url = "https://www.googleapis.com/youtube/v3/videos?";

        String partQuery = "part=snippet,contentDetails,statistics,status";
        String fieldsQuery = "&fields=pageInfo(totalResults),items(id,snippet(publishedAt,title,description,thumbnails,channelTitle,defaultAudioLanguage),contentDetails(duration,dimension),status(uploadStatus,embeddable),statistics(viewCount))";
        String lectureIdQuery = "&id=".concat(lectureId);
        String keyQuery = "&key=".concat(Secret.getGoogleCloudApiKey());

        url = url.concat(partQuery).concat(fieldsQuery).concat(lectureIdQuery).concat(keyQuery);
        log.info("youtube video detail request url : " + url);
        return url;
    }

    private String createPlaylistDetailUrl(String lectureId) {
        String url = "https://www.googleapis.com/youtube/v3/playlists?";

        String partQuery = "part=id,snippet,status,contentDetails";
        String fieldsQuery = "&fields=pageInfo,items(id,snippet(publishedAt,title,description,thumbnails,channelTitle),status,contentDetails)";
        String lectureIdQuery = "&id=".concat(lectureId);
        String keyQuery = "&key=".concat(Secret.getGoogleCloudApiKey());

        url = url.concat(partQuery).concat(fieldsQuery).concat(lectureIdQuery).concat(keyQuery);
        return url;
    }

    public JsonNode requestToYoutube(String url) throws Exception {
        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonResponse = objectMapper.readTree(response.body());
        return jsonResponse;
    }

    public KeywordSearchRes buildLectureListResponse(JsonNode resultNode) {
        List<Lecture> lectureList = new ArrayList<>();
        for (JsonNode item : resultNode.get("items")) {
            JsonNode snippetNode = item.get("snippet");
            boolean isPlaylist = item.get("id").get("kind").asText().equals("youtube#playlist");

            String lectureId;
            if (isPlaylist) {
                lectureId = item.get("id").get("playlistId").asText();
            } else {
                lectureId = item.get("id").get("videoId").asText();
            }

            Lecture lecture = Lecture.builder()
                    .lectureTitle(snippetNode.get("title").asText())
                    .description(snippetNode.get("description").asText())
                    .lectureId(lectureId)
                    .isPlaylist(isPlaylist)
                    .thumbnail(snippetNode.get("thumbnails").get("high").get("url").asText())
                    .build();
            lectureList.add(lecture);
        }
        String nextPageToken = resultNode.has("nextPageToken") ? resultNode.get("nextPageToken").asText() : null;
        String prevPageToken = resultNode.has("prevPageToken") ? resultNode.get("prevPageToken").asText() : null;

        KeywordSearchRes keywordSearchRes = KeywordSearchRes.builder()
                .nextPageToken(nextPageToken)
                .prevPageToken(prevPageToken)
                .resultPerPage(resultNode.get("pageInfo").get("resultsPerPage").asInt())
                .lectures(lectureList)
                .build();
        return keywordSearchRes;
    }

    public VideoDetail buildVideoDetailResponse(JsonNode resultNode, int indexLimit, int reviewLimit) {

        JsonNode snippetJsonNode = resultNode.get("items").get(0).get("snippet");
        String thumbnailWithHighestWidth = selectThumbnailWithHighestWidth(snippetJsonNode.get("thumbnails"));

        VideoDetail videoDetail = VideoDetail.builder()
                .lectureId(resultNode.get("items").get(0).get("id").asText())
                .lectureTitle(snippetJsonNode.get("title").asText())
                .channel(snippetJsonNode.get("channelTitle").asText())
                .description(snippetJsonNode.get("description").asText())
                .isPlaylist(false)
                .viewCount(resultNode.get("items").get(0).get("statistics").get("viewCount").asInt())
                .publishedAt(snippetJsonNode.get("publishedAt").asText().substring(0, 10))
                .thumbnail(thumbnailWithHighestWidth)
                .build();

        return videoDetail;
    }

    public PlaylistDetail buildPlaylistDetailResponse(JsonNode resultNode) {

        JsonNode snippetJsonNode = resultNode.get("items").get(0).get("snippet");
        String thumbnailWithHighestWidth = selectThumbnailWithHighestWidth(resultNode.get("items").get(0).get("snippet").get("thumbnails"));

        PlaylistDetail playlistDetail = PlaylistDetail.builder()
                .lectureId(resultNode.get("items").get(0).get("id").asText())
                .lecturedTitle(snippetJsonNode.get("title").asText())
                .channel(snippetJsonNode.get("channelTitle").asText())
                .description(snippetJsonNode.get("description").asText())
                .isPlaylist(true)
                .lectureCount(resultNode.get("items").get(0).get("contentDetails").get("itemCount").asInt())
                .thumbnail(thumbnailWithHighestWidth)
                .build();
        return playlistDetail;
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

    public String chooseTokenOrNull(String nextPageToken, String prevPageToken) {
        if (nextPageToken != null) {
            return nextPageToken;
        } else if (prevPageToken != null) {
            return prevPageToken;
        } else {
            return null;
        }
    }
}
