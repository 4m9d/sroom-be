package com.m9d.sroom.ai;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.m9d.sroom.ai.vo.MaterialResultsVo;
import com.m9d.sroom.ai.vo.MaterialVo;
import com.m9d.sroom.material.MaterialSaver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.Duration;

@Service
@Slf4j
@RequiredArgsConstructor
public class AiService {

    @Value("${ai.request-url}")
    private String gptRequestUrl;

    @Value("${ai.result-url}")
    private String gptResultUrl;

    private final WebClient webClient;

    private final MaterialSaver materialSaver;

    private final Gson gson;

    public void saveResultFromFastApi() {
        log.debug("scheduling for request fastAPI to get summary and quizzes.");

        String resultStr;
        try {
            resultStr = getResultStr(gptResultUrl);
        } catch (WebClientResponseException e) {
            log.error("Error occurred while making a request to fastAPI server. message = {}", e.getMessage());
            return;
        }

        if (resultStr == null) {
            return;
        } else {
            log.debug("response body from gpt server = {}", resultStr);
        }

        MaterialVo resultVo = getMaterialVo(resultStr);

        for (MaterialResultsVo materialVo : resultVo.getResults()) {
            saveResultEach(materialVo);
        }
    }

    private String getResultStr(String requestUrl) {
        String responseBody = null;
        try {
            responseBody = webClient.get()
                    .uri(uriBuilder -> UriComponentsBuilder
                            .fromHttpUrl(requestUrl)
                            .build()
                            .toUri())
                    .retrieve()
                    .bodyToMono(String.class)
                    .timeout(Duration.ofSeconds(4))
                    .block();
        } catch (Exception e) {
            log.error("Error occurred while making a request to fastAPI server. message = {}", e.getMessage());
            return null;
        }
        return responseBody;
    }

    private MaterialVo getMaterialVo(String resultStr) throws NullPointerException {
        MaterialVo resultVo = null;
        try {
            resultVo = gson.fromJson(resultStr, MaterialVo.class);
            if (!resultVo.getResults().isEmpty()) {
                log.info("subject = received video material, video count = {}", resultVo.getResults().size());
            }
        } catch (JsonSyntaxException e) {
            log.error("Failed to parse JSON to MaterialVo. Input JSON: {}", resultStr, e);
        }
        return resultVo;
    }

    public void saveResultEach(MaterialResultsVo materialVo) {
        try {
            materialSaver.saveMaterials(materialVo);
        } catch (Exception e) {
            log.error("failed to save summary, quizzes from GPT. error message = {}", e.getMessage(), e);
        }
    }

    public void requestToFastApi(String videoCode, String videoTitle) {
        String requestUrl = gptRequestUrl.concat("/?").concat("video_id=").concat(videoCode)
                .concat("&video_title=").concat(videoTitle);

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
