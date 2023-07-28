package com.m9d.sroom.lecture.controller;

import com.m9d.sroom.lecture.dto.response.KeywordSearch;
import com.m9d.sroom.lecture.dto.response.PlaylistDetail;
import com.m9d.sroom.member.dto.response.Login;
import com.m9d.sroom.util.ControllerTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static com.m9d.sroom.util.youtube.YoutubeConstant.DEFAULT_INDEX_COUNT;
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
                .andExpect(jsonPath("$.result_per_page").value(Integer.parseInt(limit)));
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
        KeywordSearch keywordSearch = getKeywordSearch(login, keyword);

        //expected
        mockMvc.perform(get("/lectures")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", login.getAccessToken())
                        .queryParam("keyword", keyword)
                        .queryParam("next_page_token", keywordSearch.getNextPageToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.prev_page_token").exists()) //prevPageToken을 받았다는건 첫번째 페이지가 아니라는 뜻입니다.
                .andExpect(jsonPath("$.lectures[0].lecture_code", not(keywordSearch.getLectures().get(0).getLectureCode()))); // 첫번째 반환된 페이지의 값과 다르다는 뜻입니다.
    }

    @Test
    @DisplayName("부적절한 pageToken을 입력하면 400에러가 발생합니다.")
    void searchWithPageToken400() throws Exception {
        //given
        Login login = getNewLogin();
        String notValidPageToken = "thisisnotpagetoken";

        //expected
        mockMvc.perform(get("/lectures")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", login.getAccessToken())
                        .queryParam("keyword", "keyword")
                        .queryParam("next_page_token", notValidPageToken))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("동영상 상세검색에 성공합니다.")
    void getVideoDetail200() throws Exception {
        //given
        Login login = getNewLogin();
        String lectureCode = VIDEO_CODE;

        //expected
        mockMvc.perform(get("/lectures/{lectureCode}", lectureCode)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", login.getAccessToken())
                        .queryParam("is_playlist", "false"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.lecture_title").isNotEmpty())
                .andExpect(jsonPath("$.lecture_code", is(lectureCode)))
                .andExpect(jsonPath("$.thumbnail").isNotEmpty());
    }

    @Test
    @DisplayName("재생목록 상세검색에 성공합니다.")
    void getPlaylistDetail200() throws Exception {
        //given
        Login login = getNewLogin();
        String lectureCode = PLAYLIST_CODE;

        //expected
        mockMvc.perform(get("/lectures/{lectureCode}", lectureCode)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", login.getAccessToken())
                        .queryParam("is_playlist", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.lecture_title").isNotEmpty())
                .andExpect(jsonPath("$.lecture_code", is(lectureCode)))
                .andExpect(jsonPath("$.thumbnail").isNotEmpty());
    }

    @Test
    @DisplayName("부적절한 영상 코드를 입력하면 not found error가 발생합니다.")
    void shouldReturnNotFoundErrorInvalidVideoCode() throws Exception {
        //given
        Login login = getNewLogin();
        String lectureCode = "부적절한영상코드";
        String isPlaylist = "false";

        //expected
        mockMvc.perform(get("/lectures/{lectureCode}", lectureCode)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", login.getAccessToken())
                        .queryParam("is_playlist", isPlaylist))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is("입력한 lectureId에 해당하는 영상이 없습니다.")));
    }

    @Test
    @DisplayName("부적절한 재생목록 코드를 입력하면 not found error가 발생합니다.")
    void shouldReturnNotFoundErrorInvalidPlaylistCode() throws Exception {
        //given
        Login login = getNewLogin();
        String lectureCode = "부적절한재생목록코드";
        String isPlaylist = "true";

        //expected
        mockMvc.perform(get("/lectures/{lectureCode}", lectureCode)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", login.getAccessToken())
                        .queryParam("is_playlist", isPlaylist))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is("입력한 lectureId에 해당하는 영상이 없습니다.")));
    }

    @Test
    @DisplayName("이미 등록한 강의는 registered가 true입니다.")
    void RegisteredTrueLecture() throws Exception {
        //given
        Login login = getNewLogin();
        String lectureCode = "PLRx0vPvlEmdAghTr5mXQxGpHjWqSz0dgC";
        Boolean isPlaylist = true;
        //registerLecture(login, lectureCode); // 강의를 등록합니다.

        //expected
        mockMvc.perform(get("/lectures/{lectureCode}", lectureCode)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", login.getAccessToken())
                        .queryParam("is_playlist", String.valueOf(isPlaylist)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.lectureTitle").isNotEmpty())
                .andExpect(jsonPath("$.lectureCode", is(lectureCode)))
                .andExpect(jsonPath("$.isRegistered", is("true")));
    }

    @Test
    @DisplayName("index_only = true일 경우 목차정보만 반환합니다.")
    void indexOnlyResponse200() throws Exception {
        //given
        Login login = getNewLogin();
        String lectureCode = "PLif_jr7pPZACDdM6sB6Yr_0L0VGXEjF1b";
        PlaylistDetail playlistDetail = getPlaylistDetail(login, lectureCode);
        String isPlaylist = "true";
        String indexOnly = "true";
        String indexNextToken = playlistDetail.getIndexes().getNextPageToken();

        //expected
        mockMvc.perform(get("/lectures/{lectureCode}", lectureCode)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", login.getAccessToken())
                        .queryParam("is_playlist", isPlaylist)
                        .queryParam("index_only", indexOnly)
                        .queryParam("index_next_token", indexNextToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.index_list[0].index", is(DEFAULT_INDEX_COUNT)));
    }

    @Test
    @DisplayName("review_only = true일 경우 후기정보만 반환합니다.")
    void reviewOnlyResponse200() throws Exception {
        //given
        Login login = getNewLogin();
        String lectureCode = "PLRx0vPvlEmdAghTr5mXQxGpHjWqSz0dgC";
        String isPlaylist = "true";
        String reviewOnly = "true";
        String reviewOffset = "3";
        String reviewLimit = "5";
        int reviewCount = 8;
        //registerReview(lectureCode, reviewCount);

        //expected
        mockMvc.perform(get("/lectures/{lectureCode}", lectureCode)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", login.getAccessToken())
                        .queryParam("is_playlist", isPlaylist)
                        .queryParam("review_only", reviewOnly)
                        .queryParam("review_offset", reviewOffset)
                        .queryParam("review_limit", reviewLimit))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reviews[0].index", is(reviewOffset + 1)))
                .andExpect(jsonPath("$.reviews.length()", is(Integer.parseInt(reviewLimit))));

    }

    @Test
    @DisplayName("review_only와 index_only가 모두 true일 경우 400에러가 발생합니다.")
    void twoOnlyParamTrue400() throws Exception {
        //given
        Login login = getNewLogin();
        String lectureCode = "PLRx0vPvlEmdAghTr5mXQxGpHjWqSz0dgC";
        String indexOnly = "true";
        String reviewOnly = "true";

        //expected
        mockMvc.perform(get("/lectures/{lectureCode}", lectureCode)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", login.getAccessToken())
                        .queryParam("is_playlist", "true")
                        .queryParam("review_only", reviewOnly)
                        .queryParam("index_only", indexOnly))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("reviewOnly와 indexOnly를 동시에 true로 설정할 수 없습니다.")));
    }
}
