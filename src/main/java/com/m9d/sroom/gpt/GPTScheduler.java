package com.m9d.sroom.gpt;

import com.m9d.sroom.gpt.service.GPTService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class GPTScheduler {

    private final GPTService gptService;

    @Scheduled(cron = "0/15 * * * * *")
    public void requestToGptRepeat() {
        gptService.saveResultFromFastApi();
    }
}
