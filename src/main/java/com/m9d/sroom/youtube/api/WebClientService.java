package com.m9d.sroom.youtube.api;

import com.m9d.sroom.youtube.resource.YoutubeReq;
import com.m9d.sroom.youtube.dto.playlist.PlaylistDto;
import com.m9d.sroom.youtube.dto.playlistitem.PlaylistVideoDto;
import com.m9d.sroom.youtube.dto.search.SearchDto;
import com.m9d.sroom.youtube.dto.video.VideoDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

//@Service
@Slf4j
@RequiredArgsConstructor
public class WebClientService implements YoutubeApi {

    @Value("${google.cloud-api-key}")
    private String googleCloudApiKey;

    @Value("${youtube.base-url}")
    private String baseUrl;

    private final WebClient webClient;

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

    public <T> T getYoutubeDto(YoutubeReq req, Class<T> resultClass) {
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
