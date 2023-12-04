package com.m9d.sroom.util;

import com.m9d.sroom.course.CourseService;
import com.m9d.sroom.course.dto.request.NewLecture;
import com.m9d.sroom.course.dto.response.CourseDetail;
import com.m9d.sroom.course.dto.response.EnrolledCourseInfo;
import com.m9d.sroom.member.MemberService;
import com.m9d.sroom.search.dto.response.KeywordSearchResponse;
import com.m9d.sroom.search.dto.response.PlaylistDetail;
import com.m9d.sroom.common.entity.MemberEntity;
import com.m9d.sroom.member.dto.response.Login;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mock.web.MockHttpServletResponse;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


public class ControllerTest extends SroomTest {

    @Autowired
    protected MemberService memberService;

    @Autowired
    protected JwtUtil jwtUtil;

    @Autowired
    protected CourseService courseService;

    @Test
    @DisplayName("AWS health test를 통과합니다.")
    void healthTest() throws Exception {

        //expected
        mockMvc.perform(get("/")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    protected MemberEntity getNewMemberEntity() {
        String memberCode = UUID.randomUUID().toString();
        return memberService.findOrCreateMember(memberCode);
    }

    protected Login getNewLogin() {
        MemberEntity memberEntity = getNewMemberEntity();
        return memberService.generateLogin(memberEntity, TestConstant.MEMBER_PROFILE);
    }

    protected Login getNewLogin(MemberEntity member) {
        return memberService.generateLogin(member, TestConstant.MEMBER_PROFILE);
    }

    protected KeywordSearchResponse getKeywordSearch(Login login, String keyword) throws Exception {
        MockHttpServletResponse response = mockMvc.perform(get("/lectures")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", login.getAccessToken())
                        .queryParam("keyword", keyword))
                .andExpect(status().isOk())
                .andReturn().getResponse();

        String jsonContent = response.getContentAsString();
        return objectMapper.readValue(jsonContent, KeywordSearchResponse.class);
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
                .lectureCode(TestConstant.VIDEO_CODE)
                .build();
        EnrolledCourseInfo courseId = courseService.enroll(memberId, newLecture, false, null);
        return courseId.getCourseId();
    }

    protected Long enrollNewCourseWithPlaylistSchedule(Login login) {
        Long memberId = Long.valueOf((String) jwtUtil.getDetailFromToken(login.getAccessToken()).get("memberId"));

        NewLecture newLecture = NewLecture.builder()
                .lectureCode(TestConstant.PLAYLIST_CODE)
                .scheduling(new ArrayList<>(Arrays.asList(2, 1, 2)))
                .dailyTargetTime(20)
                .expectedEndDate("2023-09-22")
                .build();
        EnrolledCourseInfo courseInfo = courseService.enroll(memberId, newLecture, true, null);
        return courseInfo.getCourseId();
    }

    protected Long enrollNewCourseWithPlaylist(Login login) {
        Long memberId = Long.valueOf((String) jwtUtil.getDetailFromToken(login.getAccessToken()).get("memberId"));

        NewLecture newLecture = NewLecture.builder()
                .lectureCode(TestConstant.PLAYLIST_CODE)
                .build();
        EnrolledCourseInfo courseInfo = courseService.enroll(memberId, newLecture, false, null);
        return courseInfo.getCourseId();
    }

    protected CourseDetail registerNewVideo(Long memberId, String videoCode) {
        EnrolledCourseInfo courseInfo = courseService.enroll(memberId, getNewLectureWithoutSchedule(videoCode),
                false, null);

        CourseDetail courseDetail = courseService.getCourseDetail(courseInfo.getCourseId());

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
