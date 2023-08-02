package com.m9d.sroom.util.youtube;

import com.m9d.sroom.lecture.exception.LectureNotFoundException;
import com.m9d.sroom.util.youtube.resource.YoutubeResource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.stream.Collectors;

import static com.m9d.sroom.util.youtube.YoutubeConstant.REQUEST_METHOD_GET;
import static com.m9d.sroom.util.youtube.YoutubeConstant.YOUTUBE_REQUEST_CONTENT_TYPE;

//@Service
@Slf4j
public class HttpUrlConnectionService implements YoutubeApi {

    @Value("${google-cloud-api-key}")
    private String googleCloudApiKey;

    @Value("${youtube.base-url}")
    private String baseUrl;

    @Override
    public Mono<String> getYoutubeVoStr(YoutubeResource resource) {
        String url = buildYoutubeApiRequest(resource.getEndpoint(), resource.getParameters());

        HttpURLConnection connection = establishConnection(url);

        String response = getAndReadResponse(connection);

        return Mono.just(response);
    }

    private String buildYoutubeApiRequest(String endPoint, Map<String, String> params) {
        String query = params.entrySet().stream()
                .map(entry -> entry.getKey() + "=" + entry.getValue())
                .collect(Collectors.joining("&"));

        String url = baseUrl + endPoint + "?" + query;
        url = url.concat("&key=" + googleCloudApiKey);

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
