package com.m9d.sroom.member.service;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier.Builder;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.m9d.sroom.member.domain.Member;
import com.m9d.sroom.member.dto.request.RefreshToken;
import com.m9d.sroom.member.dto.response.Login;
import com.m9d.sroom.member.exception.*;
import com.m9d.sroom.member.repository.MemberRepository;
import com.m9d.sroom.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;


@RequiredArgsConstructor
@Service
@Slf4j
public class MemberService {

    private final MemberRepository memberRepository;
    private final JwtUtil jwtUtil;

    @Value("${client-id}")
    private String clientId;

    @Transactional
    public Login authenticateMember(String credential) throws Exception {
        GoogleIdToken.Payload payload = getPayloadFromCredential(credential);
        String memberCode = getMemberCodeFromPayload(payload);
        Member member = findOrCreateMemberByMemberCode(memberCode);
        return generateLogin(member);
    }

    @Transactional
    public Login renewTokens(Long memberId) {
        Member member = memberRepository.findByMemberId(memberId)
                .orElseThrow(MemberNotFoundException::new);
        return generateLogin(member);
    }

    @Transactional
    public Login generateLogin(Member member) {
        String accessToken = jwtUtil.generateAccessToken(member);
        String refreshToken = jwtUtil.generateRefreshToken(member);
        memberRepository.saveRefreshToken(member.getMemberId(), refreshToken);

        Login login = Login.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .expireAt((Long) jwtUtil.getDetailFromToken(accessToken).get("expirationTime"))
                .name(member.getMemberName())
                .bio(member.getBio())
                .build();
        return login;
    }

    @Transactional
    public GoogleIdToken.Payload getPayloadFromCredential(String credential) throws Exception {
        HttpTransport transport = new NetHttpTransport();
        JacksonFactory jsonFactory = new JacksonFactory();

        GoogleIdTokenVerifier verifier = new Builder(transport, jsonFactory)
                .setAudience(Collections.singletonList(clientId))
                .build();
        GoogleIdToken idToken = verifier.verify(credential);

        if (idToken == null) {
            throw new CredentialUnauthorizedException();
        }

        return idToken.getPayload();
    }

    @Transactional
    public String getMemberCodeFromPayload(GoogleIdToken.Payload payload) {
        return payload.getSubject();
    }

    @Transactional
    public Member findOrCreateMemberByMemberCode(String memberCode) {
        return memberRepository.findByMemberCode(memberCode)
                .orElseGet(() -> createNewMember(memberCode));
    }

    @Transactional
    public Member createNewMember(String memberCode) {
        String memberName = generateMemberName();
        memberRepository.save(memberCode, memberName);
        return memberRepository.getByMemberCode(memberCode);
    }

    @Transactional
    public String generateMemberName() {
        final Random RANDOM = new Random();
        final int NUMBER_BOUND = 1000000;

        int randomNumber = RANDOM.nextInt(NUMBER_BOUND);
        String memberName = String.format("user_%06d", randomNumber);
        return memberName;
    }

    @Transactional
    public Login verifyRefreshTokenAndReturnLogin(Long memberId, RefreshToken refreshToken) {
        Map<String, Object> refreshTokenDetail = jwtUtil.getDetailFromToken(refreshToken.getRefreshToken());

        if ((Long) refreshTokenDetail.get("expirationTime") <= System.currentTimeMillis() / 1000) {
            throw new TokenExpiredException("refresh");
        }
        if (!memberId.equals(Long.valueOf((String) refreshTokenDetail.get("memberId")))) {
            throw new MemberNotMatchException();
        }

        String refreshTokenFromDB = memberRepository.getRefreshById(memberId);
        if (!refreshTokenFromDB.equals(refreshToken.getRefreshToken())) {
            throw new RefreshRenewedException();
        }

        Login login = renewTokens(memberId);
        return login;
    }
}
