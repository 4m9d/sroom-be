package com.m9d.sroom.course;

import com.m9d.sroom.course.dto.request.NewLecture;
import com.m9d.sroom.member.dto.response.Login;
import com.m9d.sroom.search.constant.SearchConstant;
import com.m9d.sroom.search.dto.request.LectureTimeRecord;
import com.m9d.sroom.util.ControllerTest;
import com.m9d.sroom.util.constant.ContentConstant;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class CourseControllerTest extends ControllerTest {

    @Test
    @DisplayName("내 강의실에서 강의코스 리스트와 정보를 불러옵니다.")
    void getCourses() throws Exception {
        //given
        Login login = getNewLogin();
        saveContents();  // 영상과 강의자료를 미리 저장하여 ai 서버를 통한 강의자료 생성을 막는 과정입니다.

        //when
        enrollNewCourseWithVideo(login);

        //then
        mockMvc.perform(get("/courses")
                        .header("Authorization", login.getAccessToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.unfinished_course").isNotEmpty())
                .andExpect(jsonPath("$.completion_rate").isNotEmpty())
                .andExpect(jsonPath("$.courses[0].course_title").value(ContentConstant.VIDEO_TITLE))
                .andDo(print());
    }

    @Test
    @DisplayName("단일영상 신규 코스등록에 성공합니다 - 일정관리 안함")
    void saveCourseWithVideo() throws Exception {
        //given
        Login login = getNewLogin();
        saveContents();

        //when
        MvcResult mvcResult = mockMvc.perform(post("/courses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", login.getAccessToken())
                        .queryParam("use_schedule", "false")
                        .content(objectMapper.writeValueAsString(
                                NewLecture.createWithoutSchedule(ContentConstant.VIDEO_CODE_LIST[0]))))
                .andReturn();
        String courseId = objectMapper.readTree(mvcResult.getResponse().getContentAsString()).path("course_id").asText();

        // then
        mockMvc.perform(get("/courses/{courseId}", courseId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", login.getAccessToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.use_schedule", is(false)))
                .andExpect(jsonPath("$.course_title", is(ContentConstant.VIDEO_TITLE)))
                .andExpect(jsonPath("$.total_video_count", is(1)))
                .andExpect(jsonPath("$.sections[0].videos[0].video_title", is(ContentConstant.VIDEO_TITLE)))
                .andDo(print());
    }

    @Test
    @DisplayName("재생목록 신규 코스등록에 성공합니다 - 일정관리 안함")
    void saveCourseWithPlaylist() throws Exception {
        //given
        Login login = getNewLogin();
        saveContents();

        //when
        MvcResult postResult = mockMvc.perform(post("/courses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", login.getAccessToken())
                        .queryParam("use_schedule", "false")
                        .content(objectMapper.writeValueAsString(
                                NewLecture.createWithoutSchedule(ContentConstant.PLAYLIST_CODE))))
                .andReturn();
        String courseId = objectMapper.readTree(postResult.getResponse().getContentAsString()).path("course_id").asText();

        //then
        mockMvc.perform(get("/courses/{courseId}", courseId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", login.getAccessToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.use_schedule", is(false)))
                .andExpect(jsonPath("$.course_title", is(ContentConstant.PLAYLIST_TITLE)))
                .andExpect(jsonPath("$.total_video_count", is(ContentConstant.VIDEO_CODE_LIST.length)))
                .andExpect(jsonPath("$.sections[0].videos", hasSize(ContentConstant.VIDEO_CODE_LIST.length)))
                .andDo(print());
    }

    @Test
    @DisplayName("재생목록 신규 코스등록에 성공합니다 - 일정관리 함")
    void saveCourseWithPlaylistSchedule() throws Exception {
        //given
        Login login = getNewLogin();
        int videoCountPerSection = 2;
        int sectionCount = 2;
        NewLecture newLecture = NewLecture.builder()
                .lectureCode(ContentConstant.PLAYLIST_CODE)
                .dailyTargetTime(60)
                .scheduling(List.of(videoCountPerSection, videoCountPerSection))
                .expectedEndDate("2024-07-29")
                .build();
        saveContents();

        //when
        MvcResult postResult = mockMvc.perform(post("/courses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", login.getAccessToken())
                        .queryParam("use_schedule", "true")
                        .content(objectMapper.writeValueAsString(newLecture)))
                .andReturn();
        String courseId = objectMapper.readTree(postResult.getResponse().getContentAsString()).path("course_id").asText();

        //then
        mockMvc.perform(get("/courses/{courseId}", courseId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", login.getAccessToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.use_schedule", is(true)))
                .andExpect(jsonPath("$.course_title", is(ContentConstant.PLAYLIST_TITLE)))
                .andExpect(jsonPath("$.total_video_count", is(ContentConstant.VIDEO_CODE_LIST.length)))
                .andExpect(jsonPath("$.sections", hasSize(sectionCount)))
                .andExpect(jsonPath("$.sections[0].videos", hasSize(videoCountPerSection)))
                .andDo(print());
    }

    @Test
    @DisplayName("단일영상 기존 코스등록에 성공합니다")
    void saveVideoInCourse() throws Exception {
        //given
        Login login = getNewLogin();
        saveContents();
        enrollNewCourseWithPlaylist(login);

        //when
        mockMvc.perform(post("/courses/{courseId}", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", login.getAccessToken())
                .queryParam("use_schedule", "true")
                .content(objectMapper.writeValueAsString(
                        NewLecture.createWithoutSchedule(ContentConstant.VIDEO_CODE_LIST[0]))));

        //expected
        mockMvc.perform(get("/courses/{courseId}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", login.getAccessToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.use_schedule", is(false)))
                .andExpect(jsonPath("$.total_video_count",
                        is(ContentConstant.VIDEO_CODE_LIST.length + 1)))
                .andExpect(jsonPath("$.sections[0].videos",
                        hasSize(ContentConstant.VIDEO_CODE_LIST.length + 1)))
                .andDo(print());
    }

    @Test
    @DisplayName("재생목록 기존 코스등록에 성공합니다")
    void savePlaylistInCourse() throws Exception {
        //given
        Login login = getNewLogin();
        saveContents();
        enrollNewCourseWithPlaylist(login);

        //when
        mockMvc.perform(post("/courses/{courseId}", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", login.getAccessToken())
                .content(objectMapper.writeValueAsString(
                        NewLecture.createWithoutSchedule(ContentConstant.PLAYLIST_CODE))));

        //expected
        mockMvc.perform(get("/courses/{courseId}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", login.getAccessToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.use_schedule", is(false)))
                .andExpect(jsonPath("$.total_video_count",
                        is(ContentConstant.VIDEO_CODE_LIST.length * 2)))
                .andExpect(jsonPath("$.sections[0].videos",
                        hasSize(ContentConstant.VIDEO_CODE_LIST.length * 2)))
                .andDo(print());
    }

    @Test
    @DisplayName("수강 페이지 정보를 적절히 받아옵니다 - 스케줄링 함")
    void getCourseDetailSchedule200() throws Exception {
        //given
        Login login = getNewLogin();
        int videoCountPerSection = 2;
        int sectionCount = 2;
        NewLecture newLecture = NewLecture.builder()
                .lectureCode(ContentConstant.PLAYLIST_CODE)
                .dailyTargetTime(60)
                .scheduling(List.of(videoCountPerSection, videoCountPerSection))
                .expectedEndDate("2024-07-29")
                .build();
        saveContents();

        //when
        enrollNewCourseWithPlaylistSchedule(login, newLecture);

        //then
        mockMvc.perform(get("/courses/{courseId}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", login.getAccessToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.use_schedule", is(true)))
                .andExpect(jsonPath("$.total_video_count",
                        is(ContentConstant.VIDEO_CODE_LIST.length)))
                .andExpect(jsonPath("$.sections[0].videos",
                        hasSize(videoCountPerSection)))
                .andExpect(jsonPath("$.sections", hasSize(sectionCount)))
                .andDo(print());
    }

    @Test
    @DisplayName("수강 페이지 정보를 적절히 받아옵니다 - 스케줄링 안함")
    void getCourseDetail200() throws Exception {
        //given
        Login login = getNewLogin();
        saveContents();

        //when
        enrollNewCourseWithPlaylist(login);

        //then
        mockMvc.perform(get("/courses/{courseId}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", login.getAccessToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.use_schedule", is(false)))
                .andExpect(jsonPath("$.total_video_count",
                        is(ContentConstant.VIDEO_CODE_LIST.length)))
                .andExpect(jsonPath("sections[0].videos", hasSize(ContentConstant.VIDEO_CODE_LIST.length)))
                .andDo(print());
    }

    @Test
    @DisplayName("강의 코스 삭제를 완료하고, 업데이트 된 강의 코스 리스트를 불러옵니다")
    void deleteCourse200() throws Exception {
        //given
        Login login = getNewLogin();
        enrollNewCourseWithPlaylist(login);

        //when
        mockMvc.perform(delete("/courses/{courseId}", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", login.getAccessToken()));

        //expected
        mockMvc.perform(get("/courses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", login.getAccessToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.courses", hasSize(0)))
                .andExpect(jsonPath("$.courses[0]").doesNotExist())
                .andDo(print());
    }

    @Test
    @DisplayName("영상 재생시간 저장을 성공합니다.")
    void saveVideoTime() throws Exception {
        //given
        Login login = getNewLogin();
        saveContents();
        enrollNewCourseWithVideo(login);
        int viewDuration = 10;

        //when
        mockMvc.perform(put("/lectures/{courseVideoId}/time", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", login.getAccessToken())
                .content(objectMapper.writeValueAsString(new LectureTimeRecord(viewDuration))));

        //then
        mockMvc.perform(get("/courses/{courseId}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", login.getAccessToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("sections[0].videos[0].last_view_duration", is(viewDuration)))
                .andExpect(jsonPath("sections[0].videos[0].max_duration", is(viewDuration)))
                .andDo(print());
    }

    @Test
    @DisplayName("영상의 수강기준 이상을 시청하면 수강이 완료됩니다.")
    void updateVideoCompletion() throws Exception {
        //given
        Login login = getNewLogin();
        saveContents();
        enrollNewCourseWithVideo(login);
        int viewDuration = (int) ((SearchConstant.MINIMUM_VIEW_PERCENT_FOR_COMPLETION + 0.1)
                * ContentConstant.VIDEO_DURATION_LIST[0]);

        //when
        mockMvc.perform(put("/lectures/{courseVideoId}/time", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", login.getAccessToken())
                .content(objectMapper.writeValueAsString(new LectureTimeRecord(viewDuration))));

        //then
        mockMvc.perform(get("/courses/{courseId}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", login.getAccessToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("sections[0].videos[0].is_completed", is(true)))
                .andExpect(jsonPath("completed_video_count", is(1)))
                .andExpect(jsonPath("$.progress", greaterThan(0)))
                .andDo(print());
    }

    @Test
    @DisplayName("'완료하기' 버튼을 누르면 영상 수강이 완료됩니다.")
    void updateCompleteManually() throws Exception {
        //given
        Login login = getNewLogin();
        saveContents();
        enrollNewCourseWithVideo(login);

        //when
        mockMvc.perform(put("/lectures/{courseVideoId}/time", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", login.getAccessToken())
                .param("isCompletedManually", "true")
                .content(objectMapper.writeValueAsString(new LectureTimeRecord(0))));

        //then
        mockMvc.perform(get("/courses/{courseId}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", login.getAccessToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("sections[0].videos[0].is_completed", is(true)))
                .andExpect(jsonPath("completed_video_count", is(1)))
                .andExpect(jsonPath("$.progress", is(100)))
                .andDo(print());
    }
}
