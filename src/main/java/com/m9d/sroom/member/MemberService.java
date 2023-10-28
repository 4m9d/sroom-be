package com.m9d.sroom.member;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.m9d.sroom.common.entity.MemberEntity;
import com.m9d.sroom.member.dto.response.Login;
import com.m9d.sroom.member.dto.response.NameUpdateResponse;
import com.m9d.sroom.member.exception.*;
import com.m9d.sroom.common.repository.member.MemberRepository;
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

        MemberEntity member = findOrCreateMemberByMemberCode(idToken.getPayload().getSubject());
        return generateLogin(member, (String) idToken.getPayload().get("picture"));
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

    public MemberEntity findOrCreateMemberByMemberCode(String memberCode) {
        return memberRepository.findByCode(memberCode)
                .orElseGet(() -> createNewMember(memberCode));
    }

    public MemberEntity createNewMember(String memberCode) {
        return memberRepository.save(MemberEntity.builder()
                .memberCode(memberCode)
                .memberName(generateMemberName())
                .build());
    }

    public String generateMemberName() {
        return String.format(DEFAULT_MEMBER_NAME_FORMAT, new Random().nextInt(NUMBER_BOUND));
    }

    @Transactional
    public Login verifyRefreshToken(String refreshToken) {
        Map<String, Object> refreshTokenDetail = jwtUtil.getDetailFromToken(refreshToken);

        if ((Long) refreshTokenDetail.get(EXPIRATION_TIME) <= System.currentTimeMillis() / MILLIS_TO_SECONDS) {
            throw new TokenExpiredException();
        }

        Long memberId = Long.valueOf((String) refreshTokenDetail.get("memberId"));

        if (!memberId.equals(Long.valueOf((String) refreshTokenDetail.get(MEMBER_ID_FIELD)))) {
            throw new MemberNotMatchException();
        }

        String refreshTokenFromDB = memberRepository.getById(memberId)
                .getRefreshToken();
        if (!refreshTokenFromDB.equals(refreshToken)) {
            throw new RefreshRenewedException();
        }

        return renewTokens(memberId, (String) refreshTokenDetail.get("profile"));
    }

    public Login renewTokens(Long memberId, String pictureUrl) {
        MemberEntity member = memberRepository.findById(memberId)
                .orElseThrow(MemberNotFoundException::new);
        return generateLogin(member, pictureUrl);
    }

    public Login generateLogin(MemberEntity member, String picture) {
        String accessToken = jwtUtil.generateAccessToken(member.getMemberId(), picture);

        member.setRefreshToken(jwtUtil.generateRefreshToken(member.getMemberId(), picture));
        memberRepository.updateById(member.getMemberId(), member);

        return Login.builder()
                .accessToken(accessToken)
                .refreshToken(member.getRefreshToken())
                .expiresAt((Long) jwtUtil.getDetailFromToken(accessToken).get(EXPIRATION_TIME))
                .name(member.getMemberName())
                .profile(picture)
                .bio(member.getBio())
                .build();
    }

    @Transactional
    public NameUpdateResponse updateMemberName(Long memberId, String name) {
        MemberEntity member = memberRepository.findById(memberId)
                .orElseThrow(MemberNotFoundException::new);
        member.setMemberName(name);
        memberRepository.updateById(memberId, member);

        return NameUpdateResponse.builder()
                .memberId(memberId)
                .name(name)
                .build();
    }
}
