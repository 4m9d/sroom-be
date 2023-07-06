package com.m9d.sroom.member.service;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.m9d.sroom.member.domain.Member;
import com.m9d.sroom.member.dto.response.Login;
import com.m9d.sroom.util.ServiceTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Transactional
public class MemberServiceTest extends ServiceTest {

    @Autowired
    private MemberService memberService;


    @Test
    @DisplayName("신규 회원을 생성합니다.")
    void createNewMember200() {
        //given
        GoogleIdToken.Payload payload = getGoogleIdTokenPayload();
        String memberCode = memberService.getMemberCodeFromPayload(payload);


        //when
        memberService.findOrCreateMember(memberCode);
        String actualResult = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM MEMBER WHERE MEMBER_CODE = ?", String.class, memberCode);


        //then
        assertThat(actualResult).isEqualTo("1");
    }

    @Test
    @DisplayName("로그인 성공 시 회원정보에 맞는 Login 객체를 반환합니다.")
    void returnLoginResponse200() {
        //given
        Member member = getNewMember();

        //when
        Login login = memberService.generateLogin(member);
        Long expectedExpirationTime = System.currentTimeMillis() / 1000 + jwtUtil.ACCESS_TOKEN_EXPIRATION_PERIOD / 1000;
        Long delta = Math.abs(expectedExpirationTime - login.getExpireIn());

        //then
        assertTrue(delta < 5);
        assertThat(login.getMemberName()).isEqualTo(member.getMemberName());
    }


}
