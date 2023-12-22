package com.m9d.sroom.material;

import com.m9d.sroom.material.dto.request.SubmittedQuizRequest;
import com.m9d.sroom.material.dto.request.SummaryEditRequest;
import com.m9d.sroom.material.model.MaterialStatus;
import com.m9d.sroom.member.dto.response.Login;
import com.m9d.sroom.util.ControllerTest;
import com.m9d.sroom.util.constant.ContentConstant;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.Matchers.*;

@Transactional
public class MaterialControllerTest extends ControllerTest {

    @Test
    @DisplayName("생성이 완료된 강의자료를 받아오는데 성공합니다.")
    void getMaterials200() throws Exception {
        //given
        Login login = getNewLogin();
        saveContents();
        enrollNewCourseWithVideo(login);

        //when, then
        mockMvc.perform(get("/materials/{courseVideoId}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", login.getAccessToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is(1)))
                .andExpect(jsonPath("$.total_quiz_count", is(ContentConstant.QUIZ_COUNT_LIST[0])))
                .andExpect(jsonPath("$.quizzes[0].id").isNotEmpty())
                .andExpect(jsonPath("$.quizzes[0].options",
                        hasSize(ContentConstant.QUIZ_OPTION_COUNT_LIST[0])))
                .andExpect(jsonPath("$.summary_brief.content").isNotEmpty());
    }

    @Test
    @DisplayName("강의노트 수정을 성공합니다.")
    void updateSummary() throws Exception {
        //given
        Login login = getNewLogin();
        saveContents();
        enrollNewCourseWithVideo(login);
        String summaryUpdated = "수정된 요약본!";

        //when
        mockMvc.perform(put("/materials/summaries/{courseVideoId}", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", login.getAccessToken())
                .content(objectMapper.writeValueAsString(new SummaryEditRequest(summaryUpdated))));


        //then
        mockMvc.perform(get("/materials/{courseVideoId}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", login.getAccessToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.summary_brief.is_modified", is(true)))
                .andExpect(jsonPath("$.summary_brief.content", is(summaryUpdated)));
    }

    @Test
    @DisplayName("퀴즈 채점 결과를 저장합니다.")
    void saveGradingResults() throws Exception {
        //given
        Login login = getNewLogin();
        saveContents();
        enrollNewCourseWithVideo(login);

        List<SubmittedQuizRequest> quizRequests = new ArrayList<>();
        for (int i = 0; i < ContentConstant.QUIZ_COUNT_LIST[0]; i++) {
            quizRequests.add(new SubmittedQuizRequest((long) i + 1, "1", true));
        }

        //when
        mockMvc.perform(post("/materials/quizzes/{courseVideoId}", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", login.getAccessToken())
                .content(objectMapper.writeValueAsString(quizRequests)));

        //then
        mockMvc.perform(get("/materials/{courseVideoId}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", login.getAccessToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.quizzes[0].is_submitted", is(true)))
                .andExpect(jsonPath("$.quizzes[0].submitted_answer").isNotEmpty())
                .andDo(print());
    }

    @Test
    @DisplayName("오답노트 등록에 성공합니다.")
    void scrapQuiz() throws Exception {
        //given
        Login login = getNewLogin();
        saveContents();
        enrollNewCourseWithVideo(login);

        List<SubmittedQuizRequest> quizRequests = new ArrayList<>();
        for (int i = 0; i < ContentConstant.QUIZ_COUNT_LIST[0]; i++) {
            quizRequests.add(new SubmittedQuizRequest((long) i + 1, "1", true));
        }

        //when
        mockMvc.perform(post("/materials/quizzes/{courseVideoId}", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", login.getAccessToken())
                .content(objectMapper.writeValueAsString(quizRequests)));
        mockMvc.perform(put("/materials/quizzes/{courseQuizId}/scrap", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", login.getAccessToken()));

        //then
        mockMvc.perform(get("/materials/{courseVideoId}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", login.getAccessToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.quizzes[0].is_scrapped", is(true)))
                .andDo(print());
    }

    @Test
    @DisplayName("pdf 변환을 위한 강의자료 불러오기 성공")
    void getMaterials4Pdf() throws Exception {
        //given
        Login login = getNewLogin();
        saveContents();
        enrollNewCourseWithVideo(login);

        //when, then
        mockMvc.perform(get("/courses/materials/{courseId}", 1)
                        .header("Authorization", login.getAccessToken()))
                .andExpect(jsonPath("$.status", is(MaterialStatus.CREATED.getValue())))
                .andExpect(jsonPath("$.materials[0].video_title", is(ContentConstant.VIDEO_TITLE)))
                .andExpect(jsonPath("$.materials[0].summary_brief.content").isNotEmpty())
                .andExpect(jsonPath("$.materials[0].quizzes[0].question").isNotEmpty())
                .andExpect(jsonPath("$.materials[0].quizzes[0].options[0]").isNotEmpty())
                .andDo(print());
    }

    @Test
    @DisplayName("강의노트 사용자 피드백이 성공합니다.")
    void feedbackSummary() throws Exception {
        //given
        Login login = getNewLogin();
        saveContents();
        enrollNewCourseWithVideo(login);
        String feedback = "{\"is_satisfactory\": true}";

        //when
        mockMvc.perform(post("/materials/{materialId}/feedback", 1)
                .header("Authorization", login.getAccessToken())
                .contentType(MediaType.APPLICATION_JSON)
                .param("type", "summary")
                .content(feedback));

        //then
        mockMvc.perform(get("/materials/{courseVideoId}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", login.getAccessToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.summary_brief.feedback_info.has_feedback", is(true)))
                .andExpect(jsonPath("$.summary_brief.feedback_info.satisfactory",
                        is(true)))
                .andDo(print());
    }

    @Test
    @DisplayName("퀴즈 사용자 피드백이 성공합니다.")
    void feedbackQuiz() throws Exception {
        //given
        Login login = getNewLogin();
        saveContents();
        enrollNewCourseWithVideo(login);
        String feedback = "{\"is_satisfactory\": false}";

        //when
        mockMvc.perform(post("/materials/{materialId}/feedback", 1)
                .header("Authorization", login.getAccessToken())
                .contentType(MediaType.APPLICATION_JSON)
                .param("type", "quiz")
                .content(feedback));

        //then
        mockMvc.perform(get("/materials/{courseVideoId}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", login.getAccessToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.quizzes[0].feedback_info.has_feedback", is(true)))
                .andExpect(jsonPath("$.quizzes[0].feedback_info.satisfactory",
                        is(false)))
                .andDo(print());
    }
}
