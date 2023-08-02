package com.m9d.sroom.util;

import com.google.common.net.HttpHeaders;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.m9d.sroom.course.dto.request.NewLecture;
import com.m9d.sroom.course.dto.response.EnrolledCourseInfo;
import com.m9d.sroom.lecture.dto.response.KeywordSearch;
import com.m9d.sroom.lecture.dto.response.PlaylistDetail;
import com.m9d.sroom.member.domain.Member;
import com.m9d.sroom.member.dto.response.Login;
import com.m9d.sroom.util.youtube.resource.LectureListReq;
import com.m9d.sroom.util.youtube.vo.search.SearchVo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


public class ControllerTest extends SroomTest {

    public Login getNewLogin() {
        Member member = getNewMember();
        Login login = memberService.generateLogin(member);
        return login;
    }

    protected Member getNewMember() {
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
        EnrolledCourseInfo courseId = courseService.enrollCourse(memberId, newLecture, false);
        return courseId.getCourseId();
    }
}
