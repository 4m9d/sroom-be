package com.m9d.sroom.util.youtube;

import com.m9d.sroom.util.youtube.resource.YoutubeResource;
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

@Service
@Slf4j
public class OkHttpClientService implements YoutubeApi {

    @Value("${google-cloud-api-key}")
    private String googleCloudApiKey;

    @Value("${youtube.base-url}")
    private String baseUrl;

    @Override
    public Mono<String> getYoutubeVoStr(YoutubeResource resource) {
        OkHttpClient client = new OkHttpClient();
        String url = buildYoutubeApiRequest(resource.getEndpoint(), resource.getParameters());

        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();
        try (Response response = client.newCall(request).execute()) {
            return Mono.just(response.body().string());
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    private String buildYoutubeApiRequest(String endPoint, Map<String, String> params) {
        String query = params.entrySet().stream()
                .map(entry -> entry.getKey() + "=" + entry.getValue())
                .collect(Collectors.joining("&"));

        String url = baseUrl + endPoint + "?" + query;
        url = url.concat("&key=" + googleCloudApiKey);

        return url;
    }
}
