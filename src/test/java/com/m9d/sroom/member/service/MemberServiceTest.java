package com.m9d.sroom.member.service;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.m9d.sroom.member.domain.Member;
import com.m9d.sroom.member.dto.request.RefreshToken;
import com.m9d.sroom.member.dto.response.Login;
import com.m9d.sroom.member.exception.MemberNotMatchException;
import com.m9d.sroom.member.exception.RefreshRenewedException;
import com.m9d.sroom.util.ServiceTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
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
        memberService.findOrCreateMemberByMemberCode(memberCode);
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
        Long delta = Math.abs(expectedExpirationTime - login.getExpireAt());

        //then
        assertTrue(delta < 5);
        assertThat(login.getName()).isEqualTo(member.getMemberName());
    }

    @Test
    @DisplayName("올바른 refresh token을 입력하면 갱신에 성공합니다.")
    void renewRefreshToken200() {
        //given
        Member member = getNewMember();
        Login login = memberService.generateLogin(member);

        //when
        Login reLogin = memberService.verifyRefreshTokenAndReturnLogin(
                member.getMemberId(),
                RefreshToken.builder().refreshToken(login.getRefreshToken()).build());

        //then
        assertNotEquals(member.getRefreshToken(), reLogin.getRefreshToken());
    }

    @Test
    @DisplayName("재로그인되면 access token이 갱신됩니다.")
    void verifyRefreshToken200() throws InterruptedException {
        //given
        Member member = getNewMember();
        Login login = memberService.generateLogin(member);
        RefreshToken refreshToken = RefreshToken.builder()
                .refreshToken(login.getRefreshToken())
                .build();

        //when
        Thread.sleep(2000);
        Login reLogin = memberService.verifyRefreshTokenAndReturnLogin(member.getMemberId(), refreshToken);

        //then
        Assertions.assertNotEquals(login.getAccessToken(), reLogin.getAccessToken());
    }

    @Test
    @DisplayName("refreshToken과 accessToken의 member정보가 다르면 400에러가 발생합니다.")
    void memberNotMatchAccessAndRefresh400() {
        //given
        Member member1 = getNewMember();

        Member member2 = getNewMember();
        Login login2 = memberService.generateLogin(member2);

        //when
        Throwable exception = null;
        try {
            memberService.verifyRefreshTokenAndReturnLogin(
                    member1.getMemberId(),
                    RefreshToken.builder()
                            .refreshToken(login2.getRefreshToken())
                            .build());
        } catch (Throwable e) {
            exception = e;
        }

        //then
        Assertions.assertNotNull(exception);
        Assertions.assertTrue(exception instanceof MemberNotMatchException);
    }

    @Test
    @DisplayName("저장된 refresh token이 아니라면 401에러가 발생합니다.")
    void refreshRenew401() throws InterruptedException {
        //given
        Member member = getNewMember();
        Login loginFirst = memberService.generateLogin(member);

        //when
        Throwable exception = null;
        Thread.sleep(2000);
        Login login = memberService.generateLogin(member); //2초 뒤 재로그인,refresh token이 갱신되어 새로 저장됩니다.
        try {
            memberService.verifyRefreshTokenAndReturnLogin(
                    member.getMemberId(),
                    RefreshToken.builder()
                            .refreshToken(loginFirst.getRefreshToken())
                            .build());
        } catch (Throwable e) {
            exception = e;
        }

        //then
        Assertions.assertNotNull(exception);
        Assertions.assertTrue(exception instanceof RefreshRenewedException);
    }


}
