package com.m9d.sroom.lecture.controller;

import com.m9d.sroom.lecture.dto.response.KeywordSearch;
import com.m9d.sroom.member.dto.response.Login;
import com.m9d.sroom.util.ControllerTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.Matchers.*;

public class LectureControllerTest extends ControllerTest {


    @Test
    @DisplayName("입력된 limit 개수만큼 검색 결과가 반환됩니다.")
    void keywordSearch200() throws Exception {
        //given
        Login login = getNewLogin();
        String keyword = "네트워크";
        String limit = String.valueOf(7);

        //expected
        mockMvc.perform(get("/lectures")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", login.getAccessToken())
                        .queryParam("keyword", keyword)
                        .queryParam("limit", limit))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultPerPage").value(Integer.parseInt(limit)));
    }

    @Test
    @DisplayName("keyword를 입력하지 않은 경우 400에러가 발생합니다.")
    void keywordSearch400() throws Exception {
        //given
        Login login = getNewLogin();

        //expected
        mockMvc.perform(get("/lectures")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", login.getAccessToken()))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("pageToken을 입력하면 다음 페이지가 반환됩니다.")
    void searchWithPageToken200() throws Exception {
        //given
        Login login = getNewLogin();
        String keyword = "네트워크";
        int resultPerPage = 8;
        KeywordSearch keywordSearch = getKeywordSearch(login, keyword, resultPerPage);

        //expected
        mockMvc.perform(get("/lectures")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", login.getAccessToken())
                        .queryParam("keyword", keyword)
                        .queryParam("nextPageToken", keywordSearch.getNextPageToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.prevPageToken").exists()) //prevPageToken을 받았다는건 첫번째 페이지가 아니라는 뜻입니다.
                .andExpect(jsonPath("$.lectures[0].lectureCode", not(keywordSearch.getLectures().get(0).getLectureCode()))); // 첫번째 반환된 페이지의 값과 다르다는 뜻입니다.
    }

    @Test
    @DisplayName("부적절한 pageToken을 입력하면 400에러가 발생합니다.")
    void searchWithPageToken400() throws Exception {
        //given
        Login login = getNewLogin();
        String notValidPageToken = "this is not page token";

        //expected
        mockMvc.perform(get("/lectures")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", login.getAccessToken())
                        .queryParam("keyword", "keyword")
                        .queryParam("nextPageToken", notValidPageToken))
                .andExpect(status().isBadRequest());
    }
}
