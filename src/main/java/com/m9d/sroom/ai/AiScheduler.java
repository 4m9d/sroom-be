package com.m9d.sroom.ai;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import static com.m9d.sroom.ai.constant.AiConstant.PERIOD_OF_GPT_REQUEST;

@Component
@Slf4j
@RequiredArgsConstructor
public class AiScheduler {

    private final AiService aiService;

    @Scheduled(cron = PERIOD_OF_GPT_REQUEST)
    public void requestToGptRepeat() {
        aiService.saveResultFromFastApi();
    }
}
