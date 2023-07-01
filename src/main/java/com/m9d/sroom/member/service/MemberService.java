package com.m9d.sroom.member.service;

import com.fasterxml.jackson.core.JsonFactory;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier.Builder;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.m9d.sroom.member.domain.Member;
import com.m9d.sroom.member.repository.MemberRepository;
import com.m9d.sroom.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;


@RequiredArgsConstructor
@Service
public class MemberService {

    private GoogleIdTokenVerifier googleIdTokenVerifier;
    private final MemberRepository memberRepository;
    private final JwtUtil jwtUtil;

    @Transactional
    public Map<String, String> authenticateUser(String clientId, String googleIdToken) throws Exception {
        HttpTransport transport = new NetHttpTransport();
        JacksonFactory jsonFactory = new JacksonFactory();

        GoogleIdTokenVerifier verifier = new Builder(transport,jsonFactory)
                .setAudience(Collections.singletonList(clientId))
                .build();
        GoogleIdToken idToken = verifier.verify(googleIdToken);
        GoogleIdToken.Payload payload = idToken.getPayload();
        String memberCode = payload.getSubject();
        Optional<Member> memberOptional = memberRepository.findByMemberCode(memberCode);
        Member member;

        if (memberOptional.isPresent()) {
            member = memberOptional.get();
        } else {
            String memberName = generateMemberName();
            memberRepository.save(memberCode, memberName);
            member = memberRepository.getByMemberCode(memberCode);
        }

        String accessToken = jwtUtil.generateAccessToken(member);
        String refreshToken = jwtUtil.generateRefreshToken(member);

        memberRepository.saveRefreshToken(member.getMemberId(), refreshToken);

        Map<String, String> tokens = new HashMap<>();
        tokens.put("access_token", accessToken);
        tokens.put("refresh_token", refreshToken);

        return tokens;
    }

    public String generateMemberName() {
        final Random RANDOM = new Random();
        final int NUMBER_BOUND = 1000000;

        int randomNumber = RANDOM.nextInt(NUMBER_BOUND);
        String memberName = String.format("user_%06d", randomNumber);
        return memberName;
    }

}
