package com.m9d.sroom.member.service;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier.Builder;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.m9d.sroom.member.domain.Member;
import com.m9d.sroom.member.dto.response.Login;
import com.m9d.sroom.member.exception.CredentialUnauthorizedException;
import com.m9d.sroom.member.repository.MemberRepository;
import com.m9d.sroom.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;


@RequiredArgsConstructor
@Service
public class MemberService {

    private final MemberRepository memberRepository;
    private final JwtUtil jwtUtil;

    @Value("${client-id}")
    private String clientId;

    @Transactional
    public Login authenticateMember(String credential) throws Exception {
        GoogleIdToken.Payload payload = getPayloadFromCredential(credential);
        String memberCode = getMemberCodeFromPayload(payload);

        Member member = findOrCreateMember(memberCode);

        String accessToken = jwtUtil.generateAccessToken(member);
        String refreshToken = jwtUtil.generateRefreshToken(member);

        memberRepository.saveRefreshToken(member.getMemberId(), refreshToken);

        Login login = Login.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .expireIn(jwtUtil.getExpirationTimeFromToken(accessToken))
                .memberName(member.getMemberName())
                .bio("")
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
    public Member findOrCreateMember(String memberCode) {
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

}
