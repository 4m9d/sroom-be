package com.m9d.sroom.member.service;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.m9d.sroom.global.mapper.Member;
import com.m9d.sroom.member.dto.request.RefreshToken;
import com.m9d.sroom.member.dto.response.Login;
import com.m9d.sroom.member.dto.response.NameUpdateResponse;
import com.m9d.sroom.member.exception.*;
import com.m9d.sroom.member.repository.MemberRepository;
import com.m9d.sroom.util.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static com.m9d.sroom.member.constant.MemberConstant.*;
import static com.m9d.sroom.util.JwtUtil.EXPIRATION_TIME;


@Service
@Slf4j
public class MemberService {

    private final MemberRepository memberRepository;
    private final JwtUtil jwtUtil;

    @Value("${client-id}")
    private String clientId;

    public MemberService(MemberRepository memberRepository, JwtUtil jwtUtil) {
        this.memberRepository = memberRepository;
        this.jwtUtil = jwtUtil;
    }

    @Transactional
    public Login authenticateMember(String credential) throws Exception {
        GoogleIdToken idToken = verifyCredential(credential);

        Member member = findOrCreateMemberByMemberCode(idToken.getPayload().getSubject());
        return generateLogin(member, (String) idToken.getPayload().get("picture"));
    }

    public GoogleIdToken verifyCredential(String credential) throws Exception {
        HttpTransport transport = new NetHttpTransport();
        GsonFactory jsonFactory = GsonFactory.getDefaultInstance();

        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(transport, jsonFactory)
                .setAudience(Collections.singletonList(clientId))
                .build();

        GoogleIdToken idToken = verifier.verify(credential);

        if (idToken == null) {
            throw new CredentialUnauthorizedException();
        }

        return idToken;
    }

    public Member findOrCreateMemberByMemberCode(String memberCode) {
        return memberRepository.findByMemberCode(memberCode)
                .orElseGet(() -> createNewMember(memberCode));
    }

    public Member createNewMember(String memberCode) {
        String memberName = generateMemberName();
        memberRepository.save(memberCode, memberName);
        return memberRepository.getByMemberCode(memberCode);
    }

    public String generateMemberName() {
        final Random RANDOM = new Random();

        int randomNumber = RANDOM.nextInt(NUMBER_BOUND);
        return String.format(DEFAULT_MEMBER_NAME_FORMAT, randomNumber);
    }

    @Transactional
    public Login verifyRefreshTokenAndReturnLogin(Long memberId, RefreshToken refreshToken) {
        Map<String, Object> refreshTokenDetail = jwtUtil.getDetailFromToken(refreshToken.getRefreshToken());

        if ((Long) refreshTokenDetail.get(EXPIRATION_TIME) <= System.currentTimeMillis() / MILLIS_TO_SECONDS) {
            throw new TokenExpiredException();
        }
        if (!memberId.equals(Long.valueOf((String) refreshTokenDetail.get(MEMBER_ID_FIELD)))) {
            throw new MemberNotMatchException();
        }

        String refreshTokenFromDB = memberRepository.getRefreshById(memberId);
        if (!refreshTokenFromDB.equals(refreshToken.getRefreshToken())) {
            throw new RefreshRenewedException();
        }

        return renewTokens(memberId, (String) refreshTokenDetail.get("profile"));
    }

    public Login renewTokens(Long memberId, String pictureUrl) {
        Member member = memberRepository.findByMemberId(memberId)
                .orElseThrow(MemberNotFoundException::new);
        return generateLogin(member, pictureUrl);
    }

    public Login generateLogin(Member member, String picture) {
        String accessToken = jwtUtil.generateAccessToken(member.getMemberId(), picture);
        String refreshToken = jwtUtil.generateRefreshToken(member.getMemberId(), picture);
        memberRepository.saveRefreshToken(member.getMemberId(), refreshToken);

        return Login.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .expiresAt((Long) jwtUtil.getDetailFromToken(accessToken).get(EXPIRATION_TIME))
                .name(member.getMemberName())
                .profile(picture)
                .bio(member.getBio())
                .build();
    }

    public NameUpdateResponse updateMemberName(Long memberId, String name) {
        Optional<Member> memberOptional = memberRepository.findByMemberId(memberId);
        if (memberOptional.isEmpty()) {
            throw new MemberNotFoundException();
        }

        Member member = memberOptional.get();
        member.setMemberName(name);
        memberRepository.updateById(memberId, member);

        return NameUpdateResponse.builder()
                .memberId(memberId)
                .name(name)
                .build();
    }
}
