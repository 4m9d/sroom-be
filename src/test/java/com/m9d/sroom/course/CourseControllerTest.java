package com.m9d.sroom.course;

import com.m9d.sroom.course.dto.request.NewLecture;
import com.m9d.sroom.member.dto.response.Login;
import com.m9d.sroom.util.ControllerTest;
import com.m9d.sroom.util.TestConstant;
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
                .lectureCode(TestConstant.VIDEO_CODE)
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
                .lectureCode(TestConstant.VIDEO_CODE)
                .dailyTargetTime(30)
                .scheduling(List.of(1))
                .expectedEndDate(expectedEndTime)
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
                .lectureCode(TestConstant.PLAYLIST_CODE)
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
                .lectureCode(TestConstant.VIDEO_CODE)
                .dailyTargetTime(60)
                .scheduling(List.of(1, 2, 3, 4))
                .expectedEndDate(expectedEndTime)
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
        newLecture.setLectureCode(TestConstant.VIDEO_CODE);

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
        newLecture.setLectureCode(TestConstant.PLAYLIST_CODE);

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
                .andExpect(jsonPath("$.sections[0].videos", hasSize(TestConstant.PLAYLIST_VIDEO_COUNT)))
                .andDo(print());
    }

    @Test
    @DisplayName("강의 코스 삭제를 완료하고, 업데이트 된 강의 코스 리스트를 불러옵니다")
    void deleteCourse200() throws Exception {
        //given
        Login login = getNewLogin();

        Long courseId1 = enrollNewCourseWithPlaylist(login);
        Long courseId2 = enrollNewCourseWithPlaylist(login);

        System.out.println(courseId1);
        System.out.println(courseId2);

        //expected
        mockMvc.perform(delete("/courses/{courseId}", courseId1)
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("Authorization", login.getAccessToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.courses", hasSize(1)))
                .andExpect(jsonPath("$.courses[0]").isNotEmpty())
                .andDo(print());
    }
}
