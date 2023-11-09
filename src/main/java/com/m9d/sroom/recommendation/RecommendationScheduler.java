package com.m9d.sroom.recommendation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

import static com.m9d.sroom.recommendation.constant.RecommendationConstant.*;

@Component
@Slf4j
@RequiredArgsConstructor
public class RecommendationScheduler {

    private final RecommendationService recommendationService;

    private final DomainRecommendation domainRecommendation;

    @PostConstruct
    @Scheduled(cron = PERIOD_OF_UPDATE_DOMAIN_RECOMMENDATIONS)
    public void temporalUpdateDomainRecommendations() {
        log.info("Update Domain Recommendations");
        domainRecommendation.setSocietyRecommendations(recommendationService.getRecommendsByDomain(SOCIETY_DOMAIN_ID));
        domainRecommendation.setScienceRecommendations(recommendationService.getRecommendsByDomain(SCIENCE_DOMAIN_ID));
        domainRecommendation.setEconomicRecommendations(recommendationService.getRecommendsByDomain(ECONOMIC_DOMAIN_ID));
        domainRecommendation.setTechRecommendations(recommendationService.getRecommendsByDomain(INFORMATION_TECH_DOMAIN_ID));
    }
}
