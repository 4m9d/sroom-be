package com.m9d.sroom.review;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class ReviewScheduler {

    private final ReviewService reviewService;

    @Scheduled(cron = "0 0/5 * * * *")
    public void requestToGptRepeat() {
        reviewService.updateRating();
    }
}
