package com.m9d.sroom.member;

import com.m9d.sroom.common.entity.jpa.MemberEntity;
import com.m9d.sroom.member.dto.response.Login;

public class MemberMapper {

    public static Login getLoginFromEntity(MemberEntity member, String accessToken, Long expiredAt, String picture) {
        return Login.builder()
                .accessToken(accessToken)
                .refreshToken(member.getRefreshToken())
                .accessExpiresAt(expiredAt)
                .name(member.getMemberName())
                .profile(picture)
                .bio(member.getBio())
                .build();
    }
}
