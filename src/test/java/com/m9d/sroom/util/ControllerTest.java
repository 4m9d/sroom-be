package com.m9d.sroom.util;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.m9d.sroom.course.dto.request.NewCourse;
import com.m9d.sroom.course.dto.response.EnrolledCourseInfo;
import com.m9d.sroom.lecture.dto.response.KeywordSearch;
import com.m9d.sroom.lecture.dto.response.PlaylistDetail;
import com.m9d.sroom.member.domain.Member;
import com.m9d.sroom.member.dto.response.Login;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


public class ControllerTest extends SroomTest{

    public Login getNewLogin() {
        Member member = getNewMember();
        Login login = memberService.generateLogin(member);
        return login;
    }

    protected Member getNewMember() {
        GoogleIdToken.Payload payload = new GoogleIdToken.Payload();
        String expectedMemberCode = "106400356559989163499";
        String memberCode = memberService.getMemberCodeFromPayload(payload.setSubject(expectedMemberCode));

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

        NewCourse newCourse = NewCourse.builder()
                .lectureCode(VIDEO_CODE)
                .channel(CHANNEL)
                .title(LECTURETITLE)
                .duration("5:22")
                .thumbnail(THUMBNAIL)
                .description(LECTURE_DESCRIPTION)
                .build();
        EnrolledCourseInfo courseId = courseService.enrollCourse(memberId, newCourse, false);
        return courseId.getCourseId();
    }
}
