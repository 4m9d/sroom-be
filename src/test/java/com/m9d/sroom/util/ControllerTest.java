package com.m9d.sroom.util;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.m9d.sroom.course.dto.request.NewLecture;
import com.m9d.sroom.course.dto.response.CourseDetail;
import com.m9d.sroom.course.dto.response.EnrolledCourseInfo;
import com.m9d.sroom.global.mapper.MemberDto;
import com.m9d.sroom.global.mapper.PlaylistDto;
import com.m9d.sroom.global.mapper.VideoDto;
import com.m9d.sroom.lecture.dto.response.KeywordSearch;
import com.m9d.sroom.lecture.dto.response.PlaylistDetail;
import com.m9d.sroom.member.dto.response.Login;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


public class ControllerTest extends SroomTest {

    protected Login getNewLogin() {
        MemberDto memberDto = getNewMember();
        return memberService.generateLogin(memberDto, "picture");
    }

    protected Login getNewLogin(MemberDto memberDto) {
        return memberService.generateLogin(memberDto, "picture");
    }

    protected MemberDto getNewMember() {
        UUID uuid = UUID.randomUUID();

        String memberCode = uuid.toString();
        return memberService.findOrCreateMemberByMemberCode(memberCode);
    }

    protected KeywordSearch getKeywordSearch(Login login, String keyword) throws Exception {
        MockHttpServletResponse response = mockMvc.perform(get("/lectures")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", login.getAccessToken())
                        .queryParam("keyword", keyword))
                .andExpect(status().isOk())
                .andReturn().getResponse();

        String jsonContent = response.getContentAsString();
        return objectMapper.readValue(jsonContent, KeywordSearch.class);
    }

    protected PlaylistDetail getPlaylistDetail(Login login, String playlistCode) throws Exception {
        MockHttpServletResponse response = mockMvc.perform(get("/lectures/{lectureCode}", playlistCode)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", login.getAccessToken())
                        .queryParam("is_playlist", "true"))
                .andExpect(status().isOk())
                .andReturn().getResponse();

        String jsonContent = response.getContentAsString();
        System.out.println(jsonContent);
        return objectMapper.readValue(jsonContent, PlaylistDetail.class);
    }

    protected Long enrollNewCourseWithVideo(Login login) {
        Object obj = jwtUtil.getDetailFromToken(login.getAccessToken()).get("memberId");
        Long memberId = Long.valueOf((String) obj);

        NewLecture newLecture = NewLecture.builder()
                .lectureCode(VIDEO_CODE)
                .build();
        EnrolledCourseInfo courseId = courseService.saveCourseWithVideo(memberId, newLecture, false);

        return courseId.getCourseId();
    }

    protected Long enrollNewCourseWithPlaylistSchedule(Login login) {
        Long memberId = Long.valueOf((String) jwtUtil.getDetailFromToken(login.getAccessToken()).get("memberId"));

        NewLecture newLecture = NewLecture.builder()
                .lectureCode(PLAYLIST_CODE)
                .scheduling(new ArrayList<>(Arrays.asList(2, 1, 2)))
                .dailyTargetTime(20)
                .expectedEndDate("2023-09-22")
                .build();
        PlaylistDto playlistDto = courseService.getPlaylistWithUpdate(newLecture.getLectureCode());
        EnrolledCourseInfo courseInfo = courseService.saveCourseWithPlaylist(memberId, newLecture, true, playlistDto);
        return courseInfo.getCourseId();
    }

    protected Long enrollNewCourseWithPlaylist(Login login) {
        Long memberId = Long.valueOf((String) jwtUtil.getDetailFromToken(login.getAccessToken()).get("memberId"));

        NewLecture newLecture = NewLecture.builder()
                .lectureCode(PLAYLIST_CODE)
                .build();
        PlaylistDto playlistDto = courseService.getPlaylistWithUpdate(newLecture.getLectureCode());
        EnrolledCourseInfo courseInfo = courseService.saveCourseWithPlaylist(memberId, newLecture, false, playlistDto);
        return courseInfo.getCourseId();
    }

    protected CourseDetail registerNewVideo(Long memberId, String videoCode) {
        EnrolledCourseInfo courseInfo = courseService.saveCourseWithVideo(memberId, getNewLectureWithoutSchedule(videoCode), false);

        CourseDetail courseDetail = courseService.getCourseDetail(memberId, courseInfo.getCourseId());

        return courseDetail;
    }

    protected NewLecture getNewLectureWithoutSchedule(String lectureCode) {
        return NewLecture.builder()
                .lectureCode(lectureCode)
                .build();
    }

    protected void insertSummaryAndQuizzes(Long courseId, Long videoId) {
    }
}
