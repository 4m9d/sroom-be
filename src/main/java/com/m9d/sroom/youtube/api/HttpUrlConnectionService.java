package com.m9d.sroom.youtube.api;

import com.google.gson.Gson;
import com.m9d.sroom.search.exception.LectureNotFoundException;
import com.m9d.sroom.youtube.YoutubeConstant;
import com.m9d.sroom.youtube.resource.YoutubeReq;
import com.m9d.sroom.youtube.dto.playlist.PlaylistDto;
import com.m9d.sroom.youtube.dto.playlistitem.PlaylistVideoDto;
import com.m9d.sroom.youtube.dto.search.SearchDto;
import com.m9d.sroom.youtube.dto.video.VideoDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

@Service
@Slf4j
public class HttpUrlConnectionService implements YoutubeApi {

    @Value("${google.cloud-api-key-1}")
    private String googleCloudApiKey1;

    @Value("${google.cloud-api-key-2}")
    private String googleCloudApiKey2;

    @Value("${google.cloud-api-key-3}")
    private String googleCloudApiKey3;

    @Value("${google.cloud-api-key-4}")
    private String googleCloudApiKey4;


    @Value("${youtube.base-url}")
    private String baseUrl;

    private final Gson gson;

    public HttpUrlConnectionService(Gson gson) {
        this.gson = gson;
    }

    @Override
    public SearchDto getSearchDto(YoutubeReq resource) {
        return getYoutubeDto(resource, SearchDto.class);
    }

    @Override
    public VideoDto getVideoDto(YoutubeReq resource) {
        return getYoutubeDto(resource, VideoDto.class);
    }

    @Override
    public PlaylistDto getPlaylistDto(YoutubeReq resource) {
        return getYoutubeDto(resource, PlaylistDto.class);
    }

    @Override
    public PlaylistVideoDto getPlaylistVideoDto(YoutubeReq resource) {
        return getYoutubeDto(resource, PlaylistVideoDto.class);
    }

    @Override
    public <T> T getYoutubeDto(YoutubeReq resource, Class<T> resultClass) {
        String url = buildYoutubeApiRequest(resource.getEndPoint(), resource.getParameters());

        HttpURLConnection connection = establishConnection(url);

        String response = getAndReadResponse(connection);

        T resultVo = gson.fromJson(response, resultClass);
        return resultVo;
    }

    private String buildYoutubeApiRequest(String endPoint, Map<String, String> params) {
        String query = params.entrySet().stream()
                .map(entry -> entry.getKey() + "=" + entry.getValue())
                .collect(Collectors.joining("&"));

        String url = baseUrl + endPoint + "?" + query;
        log.debug("youtube data api request. url = {}", url);
        url = url.concat("&key=" + getRandomApiKey());

        return url;
    }

    private String getRandomApiKey() {
        String[] apiKeys = new String[]{
                googleCloudApiKey1,
                googleCloudApiKey2,
                googleCloudApiKey3,
                googleCloudApiKey4
        };

        int index = new Random().nextInt(apiKeys.length);
        log.info("youtube data api request. api-key = {}", index);
        return apiKeys[index];
    }

    private HttpURLConnection establishConnection(String url) {
        try {
            URL requestUrl = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) requestUrl.openConnection();
            connection.setRequestMethod(YoutubeConstant.REQUEST_METHOD_GET);
            connection.setRequestProperty("Content-Type", YoutubeConstant.YOUTUBE_REQUEST_CONTENT_TYPE);
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
