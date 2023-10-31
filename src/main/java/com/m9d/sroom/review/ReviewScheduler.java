package com.m9d.sroom.review;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import static com.m9d.sroom.review.constant.ReviewConstant.PERIOD_OF_RATING_UPDATE;

@Component
@Slf4j
@RequiredArgsConstructor
public class ReviewScheduler {

    private final ReviewService reviewService;

    @Scheduled(cron = PERIOD_OF_RATING_UPDATE)
    public void requestToGptRepeat() {
        reviewService.updateRating();
    }
}
