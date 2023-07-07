package com.m9d.sroom.lecture.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@RequiredArgsConstructor
@Service
@Slf4j
public class YoutubeService {

    @Value("${google-cloud-api-key}")
    private String googleCloudApiKey;

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

    public JsonNode getLectureListFromYoutube(String keyword, int limit, String nextPageToken, String prevPageToken) throws Exception {
        String url = "https://www.googleapis.com/youtube/v3/search?";
        String pageTokenOrNull = chooseTokenOrNull(nextPageToken, prevPageToken);

        String partQuery = "part=id,snippet";
        String fieldsQuery = "&fields=nextPageToken,prevPageToken,pageInfo,items(id,snippet(title,channelTitle,thumbnails,description,publishTime))";
        String maxResultsQuery = "&maxResults=".concat(String.valueOf(limit));
        String apikeyQuery = "&key=".concat(googleCloudApiKey);
        String typeQuery = "&type=playlist,video";
        String qQuery = "&q=".concat(keyword);

        url = url.concat(partQuery).concat(fieldsQuery).concat(maxResultsQuery).concat(typeQuery).concat(apikeyQuery).concat(qQuery);
        if (pageTokenOrNull != null) {
            String pageTokenQuery = "&pageToken=".concat(pageTokenOrNull);
            url = url.concat(pageTokenQuery);
        }
        return requestToYoutube(url);
    }

    public JsonNode getVideoDetailFromYoutube(String lectureCode) throws Exception {
        String url = "https://www.googleapis.com/youtube/v3/videos?";

        String partQuery = "part=snippet,contentDetails,statistics,status";
        String fieldsQuery = "&fields=pageInfo(totalResults),items(id,snippet(publishedAt,title,description,thumbnails,channelTitle,defaultAudioLanguage),contentDetails(duration,dimension),status(uploadStatus,embeddable),statistics(viewCount))";
        String lectureCodeQuery = "&id=".concat(lectureCode);
        String keyQuery = "&key=".concat(googleCloudApiKey);

        url = url.concat(partQuery).concat(fieldsQuery).concat(lectureCodeQuery).concat(keyQuery);
        return requestToYoutube(url);
    }

    public JsonNode getPlaylistDetailFromYoutube(String lectureCode) throws Exception {
        String url = "https://www.googleapis.com/youtube/v3/playlists?";

        String partQuery = "part=id,snippet,status,contentDetails";
        String fieldsQuery = "&fields=pageInfo,items(id,snippet(publishedAt,title,description,thumbnails,channelTitle),status,contentDetails)";
        String lectureCodeQuery = "&id=".concat(lectureCode);
        String keyQuery = "&key=".concat(googleCloudApiKey);

        url = url.concat(partQuery).concat(fieldsQuery).concat(lectureCodeQuery).concat(keyQuery);
        return requestToYoutube(url);
    }

    public JsonNode getPlaylistItemsFromYoutube(String lectureCode, String nextToken, int limit) throws Exception {
        String url = "https://www.googleapis.com/youtube/v3/playlistItems?";

        String partQuery = "part=snippet,status";
        String fieldsQuery = "&fields=pageInfo,nextPageToken,prevPageToken,items(snippet(title,position,resourceId,thumbnails),status)";
        String maxResultsQuery = "&maxResults=".concat(String.valueOf(limit));
        String playlistCodeQuery = "&playlistId=".concat(lectureCode);
        String keyQuery = "&key=".concat(googleCloudApiKey);
        String pageTokenQuery = "&pageToken=".concat(nextToken);

        url = url.concat(partQuery).concat(fieldsQuery).concat(maxResultsQuery).concat(playlistCodeQuery).concat(keyQuery);

        if (nextToken != null) {
            url = url.concat(pageTokenQuery);
        }

        return requestToYoutube(url);
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
