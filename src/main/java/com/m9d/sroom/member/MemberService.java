package com.m9d.sroom.member;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.m9d.sroom.member.MemberDto;
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

import java.util.Collections;
import java.util.Map;
import java.util.Random;

import static com.m9d.sroom.member.constant.MemberConstant.*;
import static com.m9d.sroom.util.JwtUtil.EXPIRATION_TIME;


@Slf4j
@Service
public class MemberService {

    private final MemberRepository memberRepository;
    private final JwtUtil jwtUtil;

    @Value("${google.client-id}")
    private String clientId;

    public MemberService(MemberRepository memberRepository, JwtUtil jwtUtil) {
        this.memberRepository = memberRepository;
        this.jwtUtil = jwtUtil;
    }

    @Transactional
    public Login authenticateMember(String credential) throws Exception {
        GoogleIdToken idToken = verifyCredential(credential);

        MemberDto memberDto = findOrCreateMemberByMemberCode(idToken.getPayload().getSubject());
        return generateLogin(memberDto, (String) idToken.getPayload().get("picture"));
    }

    public GoogleIdToken verifyCredential(String credential) throws Exception {
        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(),
                GsonFactory.getDefaultInstance())
                .setAudience(Collections.singletonList(clientId))
                .build();

        GoogleIdToken idToken = verifier.verify(credential);

        if (idToken == null) {
            throw new CredentialUnauthorizedException();
        }

        return idToken;
    }

    public MemberDto findOrCreateMemberByMemberCode(String memberCode) {
        return memberRepository.findByCode(memberCode)
                .orElseGet(() -> createNewMember(memberCode));
    }

    public MemberDto createNewMember(String memberCode) {
        return memberRepository.save(MemberDto.builder()
                .memberCode(memberCode)
                .memberName(generateMemberName())
                .build());
    }

    public String generateMemberName() {
        return String.format(DEFAULT_MEMBER_NAME_FORMAT, new Random().nextInt(NUMBER_BOUND));
    }

    @Transactional
    public Login verifyRefreshToken(Long memberId, RefreshToken refreshToken) {
        Map<String, Object> refreshTokenDetail = jwtUtil.getDetailFromToken(refreshToken.getRefreshToken());

        if ((Long) refreshTokenDetail.get(EXPIRATION_TIME) <= System.currentTimeMillis() / MILLIS_TO_SECONDS) {
            throw new TokenExpiredException();
        }
        if (!memberId.equals(Long.valueOf((String) refreshTokenDetail.get(MEMBER_ID_FIELD)))) {
            throw new MemberNotMatchException();
        }

        String refreshTokenFromDB = memberRepository.getById(memberId)
                .getRefreshToken();
        if (!refreshTokenFromDB.equals(refreshToken.getRefreshToken())) {
            throw new RefreshRenewedException();
        }

        return renewTokens(memberId, (String) refreshTokenDetail.get("profile"));
    }

    public Login renewTokens(Long memberId, String pictureUrl) {
        MemberDto memberDto = memberRepository.findById(memberId)
                .orElseThrow(MemberNotFoundException::new);
        return generateLogin(memberDto, pictureUrl);
    }

    public Login generateLogin(MemberDto memberDto, String picture) {
        String accessToken = jwtUtil.generateAccessToken(memberDto.getMemberId(), picture);

        memberDto.setRefreshToken(jwtUtil.generateRefreshToken(memberDto.getMemberId(), picture));
        memberRepository.updateById(memberDto.getMemberId(), memberDto);

        return Login.builder()
                .accessToken(accessToken)
                .refreshToken(memberDto
                        .getRefreshToken())
                .expiresAt((Long) jwtUtil
                        .getDetailFromToken(accessToken)
                        .get(EXPIRATION_TIME))
                .name(memberDto.getMemberName())
                .profile(picture)
                .bio(memberDto.getBio())
                .build();
    }

    @Transactional
    public NameUpdateResponse updateMemberName(Long memberId, String name) {
        MemberDto memberDto = memberRepository.findById(memberId)
                .orElseThrow(MemberNotFoundException::new);
        memberDto.setMemberName(name);
        memberRepository.updateById(memberId, memberDto);

        return NameUpdateResponse.builder()
                .memberId(memberId)
                .name(name)
                .build();
    }
}
