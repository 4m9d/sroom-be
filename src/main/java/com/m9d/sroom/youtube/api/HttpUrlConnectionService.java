package com.m9d.sroom.youtube.api;

import com.google.gson.Gson;
import com.m9d.sroom.lecture.exception.LectureNotFoundException;
import com.m9d.sroom.youtube.YoutubeService;
import com.m9d.sroom.youtube.resource.YoutubeReq;
import com.m9d.sroom.youtube.dto.playlist.PlaylistDto;
import com.m9d.sroom.youtube.dto.playlistitem.PlaylistVideoDto;
import com.m9d.sroom.youtube.dto.search.SearchDto;
import com.m9d.sroom.youtube.dto.video.VideoDto;
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

@Service
@Slf4j
public class HttpUrlConnectionService implements YoutubeApi {

    @Value("${google.cloud-api-key}")
    private String googleCloudApiKey;

    @Value("${youtube.base-url}")
    private String baseUrl;

    private final Gson gson;

    public HttpUrlConnectionService(Gson gson) {
        this.gson = gson;
    }

    @Override
    public Mono<SearchDto> getSearchVo(YoutubeReq resource) {
        return getYoutubeVo(resource, SearchDto.class);
    }

    @Override
    public Mono<VideoDto> getVideoVo(YoutubeReq resource) {
        return getYoutubeVo(resource, VideoDto.class);
    }

    @Override
    public Mono<PlaylistDto> getPlaylistVo(YoutubeReq resource) {
        return getYoutubeVo(resource, PlaylistDto.class);
    }

    @Override
    public Mono<PlaylistVideoDto> getPlaylistVideoVo(YoutubeReq resource) {
        return getYoutubeVo(resource, PlaylistVideoDto.class);
    }

    @Override
    public <T> Mono<T> getYoutubeVo(YoutubeReq resource, Class<T> resultClass) {
        String url = buildYoutubeApiRequest(resource.getEndPoint(), resource.getParameters());

        HttpURLConnection connection = establishConnection(url);

        String response = getAndReadResponse(connection);

        T resultVo = gson.fromJson(response, resultClass);
        return Mono.just(resultVo);
    }

    private String buildYoutubeApiRequest(String endPoint, Map<String, String> params) {
        String query = params.entrySet().stream()
                .map(entry -> entry.getKey() + "=" + entry.getValue())
                .collect(Collectors.joining("&"));

        String url = baseUrl + endPoint + "?" + query;
        log.debug("youtube data api request. url = {}", url);
        url = url.concat("&key=" + googleCloudApiKey);

        return url;
    }

    private HttpURLConnection establishConnection(String url) {
        try {
            URL requestUrl = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) requestUrl.openConnection();
            connection.setRequestMethod(YoutubeService.REQUEST_METHOD_GET);
            connection.setRequestProperty("Content-Type", YoutubeService.YOUTUBE_REQUEST_CONTENT_TYPE);
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
