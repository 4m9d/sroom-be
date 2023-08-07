package com.m9d.sroom.course.controller;

import com.m9d.sroom.course.dto.request.NewLecture;
import com.m9d.sroom.member.dto.response.Login;
import com.m9d.sroom.util.ControllerTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class CourseControllerTest extends ControllerTest {

    @Test
    @DisplayName("내 강의실에서 강의코스 리스트와 정보를 불러옵니다.")
    void getCourses() throws Exception {
        //given
        Login login = getNewLogin();

        //expected
        mockMvc.perform(get("/courses")
                    .header("Authorization", login.getAccessToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.unfinished_course").isNotEmpty())
                .andExpect(jsonPath("$.completion_rate").isNotEmpty())
                .andDo(print());
    }

    @Test
    @DisplayName("단일영상 신규 코스등록에 성공합니다 - 일정관리 안함")
    void saveCourseWithVideo() throws Exception {
        //given
        Login login = getNewLogin();
        NewLecture newLecture = NewLecture.builder()
                .lectureCode(VIDEO_CODE)
                .build();

        String content = objectMapper.writeValueAsString(newLecture);
        String useSchedule = "false";

        //expected
        mockMvc.perform(post("/courses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", login.getAccessToken())
                        .queryParam("use_schedule", useSchedule)
                        .content(content))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.course_id").isNotEmpty())
                .andExpect(jsonPath("$.lecture_id").isNotEmpty())
                .andDo(print());
    }

    @Test
    @DisplayName("단일영상 신규 코스등록에 성공합니다 - 일정관리 함")
    void saveCourseWithVideoSchedule() throws Exception {
        //given
        Login login = getNewLogin();
        String expectedEndTime = "2023-07-29";
        NewLecture newLecture = NewLecture.builder()
                .lectureCode(VIDEO_CODE)
                .dailyTargetTime(30)
                .scheduling(List.of(1))
                .expectedEndTime(expectedEndTime)
                .build();

        String content = objectMapper.writeValueAsString(newLecture);
        String useSchedule = "true";

        //expected
        mockMvc.perform(post("/courses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", login.getAccessToken())
                        .queryParam("use_schedule", useSchedule)
                        .content(content))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.course_id").isNotEmpty())
                .andExpect(jsonPath("$.lecture_id").isNotEmpty())
                .andDo(print());
    }

    @Test
    @DisplayName("재생목록 신규 코스등록에 성공합니다 - 일정관리 안함")
    void saveCourseWithPlaylist() throws Exception {
        //given
        Login login = getNewLogin();
        NewLecture newLecture = NewLecture.builder()
                .lectureCode(PLAYLIST_CODE)
                .build();

        String content = objectMapper.writeValueAsString(newLecture);
        String useSchedule = "false";

        //expected
        mockMvc.perform(post("/courses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", login.getAccessToken())
                        .queryParam("use_schedule", useSchedule)
                        .content(content))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.course_id").isNotEmpty())
                .andExpect(jsonPath("$.lecture_id").isNotEmpty())
                .andDo(print());
    }

    @Test
    @DisplayName("재생목록 신규 코스등록에 성공합니다 - 일정관리 함")
    void saveCourseWithPlaylistSchedule() throws Exception {
        //given
        Login login = getNewLogin();
        String expectedEndTime = "2023-07-29";
        NewLecture newLecture = NewLecture.builder()
                .lectureCode(VIDEO_CODE)
                .dailyTargetTime(60)
                .scheduling(List.of(1, 2, 3, 4))
                .expectedEndTime(expectedEndTime)
                .build();

        String content = objectMapper.writeValueAsString(newLecture);
        String useSchedule = "false";

        //expected
        mockMvc.perform(post("/courses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", login.getAccessToken())
                        .queryParam("use_schedule", useSchedule)
                        .content(content))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.course_id").isNotEmpty())
                .andExpect(jsonPath("$.lecture_id").isNotEmpty())
                .andDo(print());
    }

    @Test
    @DisplayName("단일영상 기존 코스등록에 성공합니다")
    void saveVideoInCourse() throws Exception {
        //given
        Login login = getNewLogin();
        Long courseId = enrollNewCourseWithVideo(login);
        NewLecture newLecture = new NewLecture();
        newLecture.setLectureCode(VIDEO_CODE);

        String content = objectMapper.writeValueAsString(newLecture);

        //expected
        mockMvc.perform(post("/courses/{courseId}", courseId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", login.getAccessToken())
                        .content(content))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.course_id").isNotEmpty())
                .andExpect(jsonPath("$.lecture_id").isNotEmpty())
                .andDo(print());
    }

    @Test
    @DisplayName("재생목록 기존 코스등록에 성공합니다")
    void savePlaylistInCourse() throws Exception {
        //given
        Login login = getNewLogin();
        Long courseId = enrollNewCourseWithVideo(login);
        NewLecture newLecture = new NewLecture();
        newLecture.setLectureCode(PLAYLIST_CODE);

        String content = objectMapper.writeValueAsString(newLecture);

        //expected
        mockMvc.perform(post("/courses/{courseId}", courseId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", login.getAccessToken())
                        .content(content))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.course_id").isNotEmpty())
                .andExpect(jsonPath("$.lecture_id").isNotEmpty())
                .andDo(print());
    }

    @Test
    @DisplayName("수강 페이지 정보를 적절히 받아옵니다 - 스케줄링 함")
    void getCourseDetailSchedule200() throws Exception {
        //given
        Login login = getNewLogin();
        Long courseId = enrollNewCourseWithPlaylistSchedule(login);

        //expected
        mockMvc.perform(get("/courses/{courseId}", courseId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", login.getAccessToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sections", hasSize(3)))
                .andDo(print());
    }

    @Test
    @DisplayName("수강 페이지 정보를 적절히 받아옵니다 - 스케줄링 안함")
    void getCourseDetail200() throws Exception {
        //given
        Login login = getNewLogin();
        Long courseId = enrollNewCourseWithPlaylist(login);

        //expected
        mockMvc.perform(get("/courses/{courseId}", courseId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", login.getAccessToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sections", hasSize(1)))
                .andExpect(jsonPath("$.sections[0].videos", hasSize(PLAYLIST_VIDEO_COUNT)))
                .andDo(print());
    }
}
