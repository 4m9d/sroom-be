package com.m9d.sroom.youtube.api;

import com.google.gson.Gson;
import com.m9d.sroom.youtube.resource.YoutubeReq;
import com.m9d.sroom.youtube.vo.playlist.PlaylistVo;
import com.m9d.sroom.youtube.vo.playlistitem.PlaylistVideoVo;
import com.m9d.sroom.youtube.vo.search.SearchVo;
import com.m9d.sroom.youtube.vo.video.VideoVo;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;
import java.util.Map;
import java.util.stream.Collectors;

//@Service
@Slf4j
public class OkHttpClientServiceV2 implements YoutubeApiV2 {

    @Value("${google.cloud-api-key}")
    private String googleCloudApiKey;

    @Value("${youtube.base-url}")
    private String baseUrl;

    private final Gson gson;

    public OkHttpClientServiceV2(Gson gson) {
        this.gson = gson;
    }

    private String buildYoutubeApiRequest(String endPoint, Map<String, String> params) {
        String query = params.entrySet().stream()
                .map(entry -> entry.getKey() + "=" + entry.getValue())
                .collect(Collectors.joining("&"));

        String url = baseUrl + endPoint + "?" + query;
        url = url.concat("&key=" + googleCloudApiKey);

        return url;
    }

    @Override
    public SearchVo getSearchVo(YoutubeReq resource) {
        return getYoutubeVo(resource, SearchVo.class);
    }

    @Override
    public VideoVo getVideoVo(YoutubeReq resource) {
        return getYoutubeVo(resource, VideoVo.class);
    }

    @Override
    public PlaylistVo getPlaylistVo(YoutubeReq resource) {
        return getYoutubeVo(resource, PlaylistVo.class);
    }

    @Override
    public PlaylistVideoVo getPlaylistVideoVo(YoutubeReq resource) {
        return getYoutubeVo(resource, PlaylistVideoVo.class);
    }

    public <T> T getYoutubeVo(YoutubeReq resource, Class<T> resultClass) {
        OkHttpClient client = new OkHttpClient();
        String url = buildYoutubeApiRequest(resource.getEndPoint(), resource.getParameters());

        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();

        try (Response response = client.newCall(request).execute()) {
            T resultVo = gson.fromJson(response.body().string(), resultClass);
            return resultVo;
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }
}
