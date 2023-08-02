package com.m9d.sroom.util.youtube;

import com.google.gson.Gson;
import com.m9d.sroom.util.youtube.resource.YoutubeResource;
import com.m9d.sroom.util.youtube.vo.playlist.PlaylistVo;
import com.m9d.sroom.util.youtube.vo.playlistitem.PlaylistVideoVo;
import com.m9d.sroom.util.youtube.vo.search.SearchVo;
import com.m9d.sroom.util.youtube.vo.video.VideoVo;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.Map;
import java.util.stream.Collectors;

//@Service
@Slf4j
public class OkHttpClientService implements YoutubeApi {

    @Value("${google-cloud-api-key}")
    private String googleCloudApiKey;

    @Value("${youtube.base-url}")
    private String baseUrl;

    private final Gson gson;

    public OkHttpClientService(Gson gson) {
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
    public Mono<SearchVo> getSearchVo(YoutubeResource resource) {
        return getYoutubeVo(resource, SearchVo.class);
    }

    @Override
    public Mono<VideoVo> getVideoVo(YoutubeResource resource) {
        return getYoutubeVo(resource, VideoVo.class);
    }

    @Override
    public Mono<PlaylistVo> getPlaylistVo(YoutubeResource resource) {
        return getYoutubeVo(resource, PlaylistVo.class);
    }

    @Override
    public Mono<PlaylistVideoVo> getPlaylistVideoVo(YoutubeResource resource) {
        return getYoutubeVo(resource, PlaylistVideoVo.class);
    }

    public <T> Mono<T> getYoutubeVo(YoutubeResource resource, Class<T> resultClass) {
        OkHttpClient client = new OkHttpClient();
        String url = buildYoutubeApiRequest(resource.getEndpoint(), resource.getParameters());

        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();

        try (Response response = client.newCall(request).execute()) {
            T resultVo = gson.fromJson(response.body().string(), resultClass);
            return Mono.just(resultVo);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }
}
