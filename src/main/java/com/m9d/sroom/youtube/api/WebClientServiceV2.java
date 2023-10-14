package com.m9d.sroom.youtube.api;

import com.m9d.sroom.youtube.resource.YoutubeReq;
import com.m9d.sroom.youtube.vo.playlist.PlaylistVo;
import com.m9d.sroom.youtube.vo.playlistitem.PlaylistVideoVo;
import com.m9d.sroom.youtube.vo.search.SearchVo;
import com.m9d.sroom.youtube.vo.video.VideoVo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

//@Service
@Slf4j
@RequiredArgsConstructor
public class WebClientServiceV2 implements YoutubeApiV2 {

    @Value("${google.cloud-api-key}")
    private String googleCloudApiKey;

    @Value("${youtube.base-url}")
    private String baseUrl;

    private final WebClient webClient;

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

    public <T> T getYoutubeVo(YoutubeReq req, Class<T> resultClass) {
        return this.webClient
                .get()
                .uri(uriBuilder -> {
                    UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromHttpUrl(baseUrl)
                            .path(req.getEndPoint())
                            .queryParam("key", googleCloudApiKey);

                    req.getParameters().forEach(uriComponentsBuilder::queryParam);

                    return uriComponentsBuilder.build().toUri();
                })
                .retrieve()
                .bodyToMono(resultClass)
                .block();
    }
}
