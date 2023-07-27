package com.m9d.sroom.dashboard.controller;

import com.m9d.sroom.member.dto.response.Login;
import com.m9d.sroom.util.ControllerTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class DashboardControllerTest extends ControllerTest {

    @Test
    @DisplayName("유저가 식별되어 대시보드 정보를 반환합니다")
    void Dashboards200() throws Exception {
        //given
        Login login = getNewLogin();

        //expected
        mockMvc.perform(get("/dashboards")
                        .header("Authorization", login.getAccessToken()))
                        .andExpect(status().isOk())
                .andExpect(jsonPath("$.correctness_rate").isNotEmpty())
                .andExpect(jsonPath("$.completion_rate").isNotEmpty())
                .andExpect(jsonPath("$.total_learning_time").isNotEmpty());
    }
}
