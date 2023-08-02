package com.m9d.sroom.util.youtube;

import com.google.common.net.HttpHeaders;
import com.m9d.sroom.util.youtube.resource.LectureListReq;
import com.m9d.sroom.util.youtube.resource.PlaylistItemReq;
import com.m9d.sroom.util.youtube.resource.PlaylistReq;
import com.m9d.sroom.util.youtube.resource.VideoReq;
import com.m9d.sroom.util.youtube.vo.playlist.PlaylistVo;
import com.m9d.sroom.util.youtube.vo.playlistitem.PlaylistVideoVo;
import com.m9d.sroom.util.youtube.vo.search.SearchItemVo;
import com.m9d.sroom.util.youtube.vo.video.VideoVo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;

public class WebClientService implements YoutubeApi {

    @Value("${google-cloud-api-key}")
    private String googleCloudApiKey;

    @Value("${youtube.base-url}")
    private String baseUrl;

    private final WebClient webClient;

    public WebClientService() {
        this.webClient = WebClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    @Override
    public Mono<SearchItemVo> getSearchVo(LectureListReq lectureListReq) {
//        String path = baseUrl.concat(lectureListReq.getEndpoint());
//        return this.webClient.get()
//                .uri(uriBuilder -> {
//                    uriBuilder.path(path);
//                    Map<String, String> params = lectureListReq.getParameters();
//
//                    for (Map.Entry<String, String> entry : params.entrySet()) {
//                        String paramName = entry.getKey();
//                        String paramValue = entry.getValue();
//
//                        uriBuilder.queryParam(paramName, paramValue);
//                    }
//                    return uriBuilder.build();
//                })
//                .retrieve()
//                .bodyToMono(SearchItemVo.class);
        return null;
    }

    @Override
    public VideoVo getVideoVo(VideoReq videoReq) {
//        String path = baseUrl.concat(videoReq.getEndpoint());
//        return this.webClient.get()
//                .uri(uriBuilder -> {
//                    uriBuilder.path(path);
//
//                })
        return null;
    }

    @Override
    public PlaylistVo getPlaylist(PlaylistReq playlistReq) {
        return null;
    }

    @Override
    public PlaylistVideoVo getPlaylistVideo(PlaylistItemReq playlistItemReq) {
        return null;
    }
}
