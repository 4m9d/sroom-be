package com.m9d.sroom.util.youtube;

import com.m9d.sroom.util.youtube.resource.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

//@Service
@Slf4j
@RequiredArgsConstructor
public class WebClientService implements YoutubeApi {

    @Value("${google-cloud-api-key}")
    private String googleCloudApiKey;

    @Value("${youtube.base-url}")
    private String baseUrl;

    private final WebClient webClient;

    @Override
    public Mono<String> getYoutubeVoStr(YoutubeResource resource) {
        return this.webClient
                .get()
                .uri(uriBuilder -> {
                    UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromHttpUrl(baseUrl)
                            .path(resource.getEndpoint())
                            .queryParam("key", googleCloudApiKey);

                    resource.getParameters().forEach(uriComponentsBuilder::queryParam);

                    return uriComponentsBuilder.build().toUri();
                })
                .retrieve()
                .bodyToMono(String.class);
    }
}
