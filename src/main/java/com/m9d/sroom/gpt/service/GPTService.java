package com.m9d.sroom.gpt.service;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.m9d.sroom.gpt.vo.MaterialResultsVo;
import com.m9d.sroom.gpt.vo.MaterialVo;
import com.m9d.sroom.material.service.MaterialService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.util.UriComponentsBuilder;

@Service
@Slf4j
@RequiredArgsConstructor
public class GPTService {

    @Value("${gpt.request-url}")
    private String gptRequestUrl;

    @Value("${gpt.result-url}")
    private String gptResultUrl;

    private final WebClient webClient;

    private final MaterialService materialService;

    private final Gson gson;

    public void saveResultFromFastApi() {
        log.info("scheduling for request fastAPI to get summary and quizzes.");

        String resultStr;
        try {
            resultStr = getResultStr(gptResultUrl);
            log.info("response body from gpt server = {}", resultStr);
        } catch (WebClientRequestException e) {
            log.error("Error occurred while making a request to fastAPI server. message = {}", e.getMessage());
            return;
        }

        MaterialVo resultVo = getMaterialVo(resultStr);

        for (MaterialResultsVo materialVo : resultVo.getResults()) {
            saveResultEach(materialVo);
        }
    }

    private String getResultStr(String requestUrl) throws WebClientRequestException {
        String responseBody = null;
        try {
            responseBody = webClient.get()
                    .uri(uriBuilder -> UriComponentsBuilder
                            .fromHttpUrl(requestUrl)
                            .build()
                            .toUri())
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
        } catch (WebClientResponseException e) {
            log.error("fastAPI server not available. error code = {}, message = {}", e.getStatusCode(), e.getMessage());
        }
        return responseBody;
    }

    private MaterialVo getMaterialVo(String resultStr) throws NullPointerException {
        MaterialVo resultVo = null;
        try {
            resultVo = gson.fromJson(resultStr, MaterialVo.class);
            log.info("received video material count is = {}", resultVo.getResults().size());
        } catch (JsonSyntaxException e) {
            log.error("Failed to parse JSON to MaterialVo. Input JSON: {}", resultStr, e);
        }
        return resultVo;
    }

    public void saveResultEach(MaterialResultsVo materialVo) {
        try {
            materialService.saveMaterials(materialVo);
        } catch (Exception e) {
            log.error("failed to save summary, quizzes from GPT. error message = {}", e.getMessage(), e);
        }
    }

    public void requestToFastApi(String videoCode) {
        String requestUrl = gptRequestUrl.concat("/?").concat("video_id=").concat(videoCode);

        try {
            HttpStatus status = webClient.get()
                    .uri(requestUrl)
                    .retrieve()
                    .toBodilessEntity()
                    .block()
                    .getStatusCode();
            if (status == HttpStatus.ACCEPTED) {
                log.debug("Received 202 status code");
            } else {
                log.debug("Unexpected status code: " + status);
            }
        } catch (WebClientResponseException e) {
            log.debug("Error status code: " + e.getStatusCode());
        } catch (Exception e) {
            log.debug("Error: " + e.getMessage());
        }
    }
}
