package com.m9d.sroom.dashboard;

import com.m9d.sroom.member.dto.response.Login;
import com.m9d.sroom.util.ControllerTest;
import com.m9d.sroom.util.constant.ContentConstant;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
public class DashboardControllerTest extends ControllerTest {

    @Test
    @DisplayName("유저가 식별되어 대시보드 정보를 반환합니다")
    void Dashboards200() throws Exception {
        //given
        Login login = getNewLogin();
        saveContents();
        enrollNewCourseWithVideo(login);


        //expected
        mockMvc.perform(get("/dashboards")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", login.getAccessToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.correctness_rate").isNotEmpty())
                .andExpect(jsonPath("$.completion_rate").isNotEmpty())
                .andExpect(jsonPath("$.total_learning_time").isNotEmpty())
                .andExpect(jsonPath("$.latest_lectures[0].course_title").value(ContentConstant.VIDEO_TITLE));
    }
}
