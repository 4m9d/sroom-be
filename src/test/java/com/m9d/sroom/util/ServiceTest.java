package com.m9d.sroom.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.m9d.sroom.SroomApplicationTests;
import com.m9d.sroom.lecture.service.YoutubeService;
import com.m9d.sroom.member.domain.Member;
import com.m9d.sroom.member.repository.MemberRepository;
import com.m9d.sroom.member.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest
@Testcontainers
public class ServiceTest extends SroomApplicationTests {

    @Autowired
    protected MemberRepository memberRepository;

    @Autowired
    protected MemberService memberService;

    @Autowired
    protected JdbcTemplate jdbcTemplate;

    @Autowired
    protected JwtUtil jwtUtil;

    @Autowired
    protected YoutubeService youtubeService;

    @Autowired
    protected ObjectMapper objectMapper;

    protected GoogleIdToken.Payload getGoogleIdTokenPayload() {
        GoogleIdToken.Payload payload = new GoogleIdToken.Payload();
        String expectedMemberCode = "106400356559989163499";
        payload.setSubject(expectedMemberCode);

        return payload;
    }

    protected Member getNewMember() {
        String memberCode = memberService.getMemberCodeFromPayload(getGoogleIdTokenPayload());
        return memberService.findOrCreateMember(memberCode);
    }

}
