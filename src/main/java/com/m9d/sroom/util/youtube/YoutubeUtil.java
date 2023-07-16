package com.m9d.sroom.util.youtube;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.m9d.sroom.lecture.exception.LectureNotFoundException;
import com.m9d.sroom.util.youtube.resource.YoutubeResource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
public class YoutubeUtil {

    @Value("${google-cloud-api-key}")
    private String googleCloudApiKey;

    public <T extends YoutubeResource> CompletableFuture<JsonNode> getYoutubeResource(T resource) throws Exception {
        String url = buildYoutubeApiRequest(resource.getEndpoint(), resource.getParameters());

        JsonNode resultNode = requestToYoutube(url);
        return CompletableFuture.completedFuture(resultNode);
    }

    public JsonNode requestToYoutube(String url) throws Exception {
        HttpURLConnection connection = establishConnection(url);
        String response = getAndReadResponse(connection);

        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readTree(response);
    }

    private String buildYoutubeApiRequest(String baseUrl, Map<String, String> params) {
        String query = params.entrySet().stream()
                .map(entry -> entry.getKey() + "=" + entry.getValue())
                .collect(Collectors.joining("&"));

        String url = encodeSpaces(baseUrl + query);
        url = url.concat("&key=" + googleCloudApiKey);

        return url;
    }

    private HttpURLConnection establishConnection(String url) throws IOException {
        URL requestUrl = new URL(url);
        HttpURLConnection connection = (HttpURLConnection) requestUrl.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Content-Type", "application/json");
        return connection;
    }

    private String getAndReadResponse(HttpURLConnection connection) throws IOException {
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
    }

    public String encodeSpaces(String input) {
        return input.replace(" ", "%20");
    }
}
