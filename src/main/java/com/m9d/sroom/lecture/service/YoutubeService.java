package com.m9d.sroom.lecture.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.m9d.sroom.config.secret.Secret;
import com.m9d.sroom.lecture.dto.response.KeywordSearchRes;
import com.m9d.sroom.lecture.dto.response.Lecture;
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

        String uri = createSearchURI(keyword, limit, nextPageToken, prevPageToken);
        JsonNode resultNode = SearchRequestToYoutube(uri);

        KeywordSearchRes keywordSearchRes = buildResponse(resultNode);
        return keywordSearchRes;
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
        log.info("youtube request uri:" + url);
        return url;
    }

    public JsonNode SearchRequestToYoutube(String uri) throws Exception {
        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(uri))
                .header("Content-Type", "application/json")
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonResponse = objectMapper.readTree(response.body());
        return jsonResponse;
    }
    public KeywordSearchRes buildResponse(JsonNode resultNode){
        List<Lecture> lectureList = new ArrayList<>();
        for (JsonNode item : resultNode.get("items")) {
            JsonNode snippetNode = item.get("snippet");
            boolean isPlaylist = item.get("id").get("kind").asText().equals("youtube#playlist");

            String lectureId;
            if(isPlaylist){
                lectureId = item.get("id").get("playlistId").asText();
            }else{
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
