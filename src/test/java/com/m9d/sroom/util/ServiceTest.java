package com.m9d.sroom.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.m9d.sroom.course.service.CourseService;
import com.m9d.sroom.dashbord.service.DashboardService;
import com.m9d.sroom.lecture.service.LectureService;
import com.m9d.sroom.member.domain.Member;
import com.m9d.sroom.member.repository.MemberRepository;
import com.m9d.sroom.member.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.UUID;

@SpringBootTest
@AutoConfigureTestDatabase
public class ServiceTest {

    @Autowired
    protected MemberRepository memberRepository;

    @Autowired
    protected MemberService memberService;

    @Autowired
    protected JdbcTemplate jdbcTemplate;

    @Autowired
    protected JwtUtil jwtUtil;

    @Autowired
    protected LectureService lectureService;

    @Autowired
    protected CourseService courseService;

    @Autowired
    protected DashboardService dashboardService;

    @Autowired
    protected ObjectMapper objectMapper;

    protected GoogleIdToken.Payload getGoogleIdTokenPayload() {
        GoogleIdToken.Payload payload = new GoogleIdToken.Payload();

        // Generate a UUID
        UUID uuid = UUID.randomUUID();

        // Convert it to a string
        String randomUUIDString = uuid.toString();
        String expectedMemberCode = randomUUIDString;
        payload.setSubject(expectedMemberCode);

        return payload;
    }

    protected Member getNewMember() {
        String memberCode = memberService.getMemberCodeFromPayload(getGoogleIdTokenPayload());
        return memberService.findOrCreateMemberByMemberCode(memberCode);
    }

}
