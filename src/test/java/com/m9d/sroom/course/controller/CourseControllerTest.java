package com.m9d.sroom.course.controller;

import com.m9d.sroom.course.dto.request.NewLecture;
import com.m9d.sroom.member.dto.response.Login;
import com.m9d.sroom.util.ControllerTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class CourseControllerTest extends ControllerTest {

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
}
