package com.m9d.sroom.member;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.m9d.sroom.common.entity.jpa.MemberEntity;
import com.m9d.sroom.common.repository.member.MemberJpaRepository;
import com.m9d.sroom.member.dto.response.Login;
import com.m9d.sroom.member.dto.response.NameUpdateResponse;
import com.m9d.sroom.member.exception.*;
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
public class MemberServiceVJpa {

    private final MemberJpaRepository memberRepository;
    private final JwtUtil jwtUtil;

    @Value("${google.client-id}")
    private String clientId;

    public MemberServiceVJpa(MemberJpaRepository memberRepository, JwtUtil jwtUtil) {
        this.memberRepository = memberRepository;
        this.jwtUtil = jwtUtil;
    }

    @Transactional
    public Login authenticateMember(String credential) throws Exception {
        GoogleIdToken idToken = verifyCredential(credential);
        String memberCode = idToken.getPayload().getSubject();
        String picture = (String) idToken.getPayload().get("picture");

        MemberEntity memberEntity = memberRepository.findByCode(memberCode)
                .orElseGet(() -> createNewMember(memberCode));
        log.info("member login. memberId = {}", memberEntity.getMemberId());

        return login(memberEntity, picture);
    }

    private GoogleIdToken verifyCredential(String credential) throws Exception {
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

    private MemberEntity createNewMember(String memberCode) {
        return memberRepository.save(MemberEntity.create(memberCode, generateMemberName()));
    }

    private String generateMemberName() {
        return String.format(DEFAULT_MEMBER_NAME_FORMAT, new Random().nextInt(NUMBER_BOUND));
    }

    private Login login(MemberEntity memberEntity, String picture) {
        String accessToken = jwtUtil.generateAccessToken(memberEntity.getMemberId(), picture);
        memberEntity.updateRefreshToken(jwtUtil.generateRefreshToken(memberEntity.getMemberId(), picture));

        return MemberMapper.getLoginFromEntity(memberEntity, accessToken,
                (Long) jwtUtil.getDetailFromToken(accessToken).get(EXPIRATION_TIME), picture);
    }

    @Transactional
    public Login verifyRefreshToken(String refreshToken) {
        Map<String, Object> refreshTokenDetail = jwtUtil.getDetailFromToken(refreshToken);

        if ((Long) refreshTokenDetail.get(EXPIRATION_TIME) <= System.currentTimeMillis() / MILLIS_TO_SECONDS) {
            throw new TokenExpiredException();
        }

        Long memberId = Long.valueOf((String) refreshTokenDetail.get(MEMBER_ID_FIELD));

        if (!memberId.equals(Long.valueOf((String) refreshTokenDetail.get(MEMBER_ID_FIELD)))) {
            throw new MemberNotMatchException();
        }

        MemberEntity memberEntity = memberRepository.findById(memberId)
                .orElseThrow(MemberNotFoundException::new);

        if (!memberEntity.getRefreshToken().equals(refreshToken)) {
            throw new RefreshRenewedException();
        }

        return login(memberEntity, (String) refreshTokenDetail.get("profile"));
    }

    @Transactional
    public NameUpdateResponse updateMemberName(Long memberId, String newName) {
        MemberEntity memberEntity = memberRepository.findById(memberId)
                .orElseThrow(MemberNotFoundException::new);

        memberEntity.updateName(newName);

        return NameUpdateResponse.builder()
                .memberId(memberEntity.getMemberId())
                .name(memberEntity.getMemberName())
                .build();
    }

    @Transactional
    public MemberEntity getMemberEntity(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(MemberNotFoundException::new);
    }
}