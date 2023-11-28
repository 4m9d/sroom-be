package com.m9d.sroom.member;

import com.m9d.sroom.member.dto.request.GoogleIdKey;
import com.m9d.sroom.member.dto.request.RefreshToken;
import com.m9d.sroom.util.ControllerTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
public class MemberControllerTest extends ControllerTest {

    @Test
    @DisplayName("유효하지 않거나 만료된 구글 토큰의 경우 회원가입이 불가합니다.")
    void signup401() throws Exception {
        //given
        GoogleIdKey googleIdKey = GoogleIdKey.builder()
                .credential("eyJhbGciOiJSUzI1NiIsImtpZCI6IjkzNDFkZWRlZWUyZDE4NjliNjU3ZmE5MzAzMDAwODJmZTI2YjNkOTIiLCJ0eXAiOiJKV1QifQ.eyJpc3MiOiJodHRwczovL2FjY291bnRzLmdvb2dsZS5jb20iLCJuYmYiOjE2ODg0NTIzNDYsImF1ZCI6IjEwODUyMjEwNDc5NDYtNHMxZ2YwZjlqdHQ3OTB2a2ZkcHZ1Z3FqbW9qMzdrdGsuYXBwcy5nb29nbGV1c2VyY29udGVudC5jb20iLCJzdWIiOiIxMDY0MDAzNTY1NTk5ODkxNjM0OTQiLCJlbWFpbCI6ImpkdTIxMjFAZ21haWwuY29tIiwiZW1haWxfdmVyaWZpZWQiOnRydWUsImF6cCI6IjEwODUyMjEwNDc5NDYtNHMxZ2YwZjlqdHQ3OTB2a2ZkcHZ1Z3FqbW9qMzdrdGsuYXBwcy5nb29nbGV1c2VyY29udGVudC5jb20iLCJuYW1lIjoi7KCV65GQ7JuQIiwicGljdHVyZSI6Imh0dHBzOi8vbGgzLmdvb2dsZXVzZXJjb250ZW50LmNvbS9hL0FBY0hUdGZvSnlKYXBWZzdfS0QyQXN2aUhCWG5MaVhZMVFNSV9DcmpyTGNYYVZaRj1zOTYtYyIsImdpdmVuX25hbWUiOiLrkZDsm5AiLCJmYW1pbHlfbmFtZSI6IuyglSIsImlhdCI6MTY4ODQ1MjY0NiwiZXhwIjoxNjg4NDU2MjQ2LCJqdGkiOiIxYjdkMWI2NmE3YTE1Zjc0MDM3Y2EyOWVhNmFiNzhkZTA5NjgwNzZhIn0.BXj2pNPTMSn4qHexsdCxN8ettcR1RNSTlyb5qX9FK5ncZepBRs6QFYYY87c0pYRNNbmxI6Bmr9Q2GnL-JrxiAAtJIixLeSWjMmFevrh9OAFBQSfehBGCCf6_THZ40zptnjIVTxSGHtagJymawo2UWewvypaPpHDap_IBZxwkQ2nkOqSQY8U8itMmTjsHstTnvFzNYq-aKhDu_W9blW17f0kuXegcMTETUUEVzDGFSusHN5uLrnlI5ITuPXpWAI2ZdmI0XXiz7ddi_EzjdiJ-yOLwtFatmW3kIY0CwkO3dHf6sYab1-MFy8Vje_gCbWw6s6H3NFCj-juHEp_cCUAKEA")
                .build();
        String googleIdKeyJson = objectMapper.writeValueAsString(googleIdKey);

        //expected
        mockMvc.perform(post("/members/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(googleIdKeyJson))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("access token을 입력하지 않으면 토큰갱신이 불가합니다.")
    void refresh401() throws Exception {
        //given
        RefreshToken refreshToken = RefreshToken.builder()
                .refreshToken("eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxIiwiaWF0IjoxNjg4OTY3MjE5LCJleHAiOjE2ODk1NzIwMTl9.H7uqypveQrQ5dBvDtdkipIFlz749BlTXtKQ93_pQff4")
                .build();
        String refreshTokenJson = objectMapper.writeValueAsString(refreshToken);

        //expected
        mockMvc.perform(post("/members/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(refreshTokenJson))
                .andExpect(status().isUnauthorized());
    }
}
