package com.m9d.sroom.lecture.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.CompletableFuture;

@RequiredArgsConstructor
@Service
@Slf4j
public class YoutubeService {

    @Value("${google-cloud-api-key}")
    private String googleCloudApiKey;

    public JsonNode requestToYoutube(String url) throws Exception {
        URL requestUrl = new URL(url);
        HttpURLConnection connection = (HttpURLConnection) requestUrl.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Content-Type", "application/json");

        int responseCode = connection.getResponseCode();

        if (responseCode == HttpURLConnection.HTTP_OK) {
            InputStream inputStream = connection.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

            StringBuilder responseBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                responseBuilder.append(line);
            }
            reader.close();

            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonResponse = objectMapper.readTree(responseBuilder.toString());

            return jsonResponse;
        } else {
            throw new RuntimeException("Failed to make HTTP request. Response code: " + responseCode);
        }
    }

    private void validateUrl(String url) {
        if (url.contains(" ")) {
            throw new IllegalArgumentException("url에는 띄어쓰기가 허용되지 않습니다.");
        }
    }

    public String encodeSpaces(String input) {
        return input.replace(" ", "%20");
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
        url = encodeSpaces(url);

        return requestToYoutube(url);
    }

    public JsonNode getVideoDetailFromYoutube(String lectureCode) throws Exception {
        String url = "https://www.googleapis.com/youtube/v3/videos?";

        String partQuery = "part=snippet,contentDetails,statistics,status";
        String fieldsQuery = "&fields=pageInfo(totalResults),items(id,snippet(publishedAt,title,description,thumbnails,channelTitle,defaultAudioLanguage),contentDetails(duration,dimension),status(uploadStatus,embeddable),statistics(viewCount))";
        String lectureCodeQuery = "&id=".concat(lectureCode);
        String keyQuery = "&key=".concat(googleCloudApiKey);

        url = url.concat(partQuery).concat(fieldsQuery).concat(lectureCodeQuery).concat(keyQuery);
        validateUrl(url);
        return requestToYoutube(url);
    }

    public JsonNode getPlaylistDetailFromYoutube(String lectureCode) throws Exception {
        String url = "https://www.googleapis.com/youtube/v3/playlists?";

        String partQuery = "part=id,snippet,status,contentDetails";
        String fieldsQuery = "&fields=pageInfo,items(id,snippet(publishedAt,title,description,thumbnails,channelTitle),status,contentDetails)";
        String lectureCodeQuery = "&id=".concat(lectureCode);
        String keyQuery = "&key=".concat(googleCloudApiKey);

        url = url.concat(partQuery).concat(fieldsQuery).concat(lectureCodeQuery).concat(keyQuery);
        validateUrl(url);
        return requestToYoutube(url);
    }

    public JsonNode getPlaylistItemsFromYoutube(String lectureCode, String nextToken, int limit) throws Exception {
        String url = "https://www.googleapis.com/youtube/v3/playlistItems?";

        String partQuery = "part=snippet,status";
        String fieldsQuery = "&fields=pageInfo,nextPageToken,prevPageToken,items(snippet(title,position,resourceId,thumbnails),status)";
        String maxResultsQuery = "&maxResults=".concat(String.valueOf(limit));
        String playlistCodeQuery = "&playlistId=".concat(lectureCode);
        String keyQuery = "&key=".concat(googleCloudApiKey);

        url = url.concat(partQuery).concat(fieldsQuery).concat(maxResultsQuery).concat(playlistCodeQuery).concat(keyQuery);

        if (nextToken != null) {
            String pageTokenQuery = "&pageToken=".concat(nextToken);
            url = url.concat(pageTokenQuery);
        }

        validateUrl(url);
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
