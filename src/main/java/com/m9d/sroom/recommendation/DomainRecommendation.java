package com.m9d.sroom.recommendation;

import com.m9d.sroom.recommendation.dto.RecommendLecture;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@Data
public class DomainRecommendation {

    private List<RecommendLecture> societyRecommendations;

    private List<RecommendLecture> scienceRecommendations;

    private List<RecommendLecture> economicRecommendations;

    private List<RecommendLecture> techRecommendations;

}
