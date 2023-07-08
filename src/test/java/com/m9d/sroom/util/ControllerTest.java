package com.m9d.sroom.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.m9d.sroom.SroomApplicationTests;
import com.m9d.sroom.lecture.dto.response.KeywordSearch;
import com.m9d.sroom.lecture.dto.response.PlaylistDetail;
import com.m9d.sroom.member.domain.Member;
import com.m9d.sroom.member.dto.response.Login;
import com.m9d.sroom.member.repository.MemberRepository;
import com.m9d.sroom.member.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
public class ControllerTest extends SroomApplicationTests {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    protected MemberService memberService;

    @Autowired
    protected MemberRepository memberRepository;

    @Autowired
    protected JwtUtil jwtUtil;

    @Autowired
    protected JdbcTemplate jdbcTemplate;

    public Login getNewLogin() {
        Member member = getNewMember();
        Login login = memberService.generateLogin(member);
        return login;
    }

    protected Member getNewMember() {
        GoogleIdToken.Payload payload = new GoogleIdToken.Payload();
        String expectedMemberCode = "106400356559989163499";
        String memberCode = memberService.getMemberCodeFromPayload(payload.setSubject(expectedMemberCode));

        return memberService.findOrCreateMember(memberCode);
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

    protected PlaylistDetail getPlaylistDetail(Login login, String playlistCode, int indexLimit) throws Exception {
        MockHttpServletResponse response = mockMvc.perform(get("/lectures/{lectureCode}", playlistCode)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", login.getAccessToken())
                        .queryParam("is_playlist", "true")
                        .queryParam("index_limit", String.valueOf(indexLimit)))
                .andExpect(status().isOk())
                .andReturn().getResponse();

        String jsonContent = response.getContentAsString();
        return objectMapper.readValue(jsonContent, PlaylistDetail.class);
    }
}
