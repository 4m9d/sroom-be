package com.m9d.sroom.util.youtube;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.m9d.sroom.lecture.exception.LectureNotFoundException;
import com.m9d.sroom.util.youtube.resource.YoutubeResource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static com.m9d.sroom.util.youtube.YoutubeConstant.*;

@Service
@Slf4j
public class YoutubeUtil {

    @Value("${google-cloud-api-key}")
    private String googleCloudApiKey;

    @Value("${youtube.base-url}")
    private String baseUrl;

    public <T extends YoutubeResource> CompletableFuture<JsonNode> getYoutubeResource(T resource) {
        String url = buildYoutubeApiRequest(resource.getEndpoint(), resource.getParameters());

        JsonNode resultNode = requestToYoutube(url);
        return CompletableFuture.completedFuture(resultNode);
    }

    public JsonNode requestToYoutube(String url) {
        HttpURLConnection connection = establishConnection(url);
        String response = getAndReadResponse(connection);

        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readTree(response);
        } catch (Exception e) {
            log.info("error occurred. message: read tree failed (youtube response)");
            throw new RuntimeException(e);
        }
    }

    private String buildYoutubeApiRequest(String endPoint, Map<String, String> params) {
        String query = params.entrySet().stream()
                .map(entry -> entry.getKey() + "=" + entry.getValue())
                .collect(Collectors.joining("&"));

        String url = baseUrl + endPoint + query;
        url = url.concat("&key=" + googleCloudApiKey);

        System.out.println(url);

        return url;
    }

    private HttpURLConnection establishConnection(String url) {
        try {
            URL requestUrl = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) requestUrl.openConnection();
            connection.setRequestMethod(REQUEST_METHOD_GET);
            connection.setRequestProperty("Content-Type", YOUTUBE_REQUEST_CONTENT_TYPE);
            return connection;
        } catch (Exception e) {
            log.info("error occurred. message: open connection youtube failed");
            throw new RuntimeException(e);
        }
    }

    private String getAndReadResponse(HttpURLConnection connection) {
        try {
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
                return responseBuilder.toString();
            } else {
                throw new LectureNotFoundException();
            }
        } catch (IOException e) {
            log.info("error occurred. message: get response from youtube failed");
            throw new RuntimeException(e);
        }
    }
}
