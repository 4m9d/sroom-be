package com.m9d.sroom.recommendation;

import com.m9d.sroom.member.dto.response.Login;
import com.m9d.sroom.util.ControllerTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@Transactional
public class RecommendationControllerTest extends ControllerTest {

    @Autowired
    private RecommendationScheduler recommendationScheduler;

    @Test
    @DisplayName("추천 리스트를 불러옵니다.")
    void Recommendation200() throws Exception {
        //given
        Login login = getNewLogin();
        saveContents();
        enrollNewCourseWithVideo(login);
        recommendationScheduler.temporalUpdateDomainRecommendations();

        //expected
        mockMvc.perform(get("/lectures/recommendations")
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("Authorization", login.getAccessToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.general_recommendations", hasSize(4)))
                .andExpect(jsonPath("$.channel_recommendations", hasSize(4)))
                .andExpect(jsonPath("$.society_recommendations", hasSize(1)))
                .andExpect(jsonPath("$.science_recommendations", hasSize(1)))
                .andExpect(jsonPath("$.economic_recommendations", hasSize(1)))
                .andExpect(jsonPath("$.tech_recommendations", hasSize(1)));
    }
}
