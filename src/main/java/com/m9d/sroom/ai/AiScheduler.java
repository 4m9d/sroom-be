package com.m9d.sroom.ai;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class AiScheduler {

    private final AiService aiService;

    @Scheduled(cron = "0/5 * * * * *")
    public void requestToGptRepeat() {
        aiService.saveResultFromFastApi();
    }
}
