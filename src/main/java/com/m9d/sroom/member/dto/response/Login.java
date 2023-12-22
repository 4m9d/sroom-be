package com.m9d.sroom.member.dto.response;

import com.m9d.sroom.common.entity.jpa.MemberEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Schema(description = "로그인 정보")
@Data
@Builder
public class Login {

    @Schema(description = "엑세스 토큰", example = "eyJhbGci...")
    private String accessToken;

    @Schema(description = "리프레시 토큰", example = "eyJhbGci...")
    private String refreshToken;

    @Schema(description = "만료 시간(초)", example = "1688398507")
    private Long accessExpiresAt;

    @Schema(description = "멤버 이름", example = "user_978538")
    private String name;

    @Schema(description = "멤버 프로필 사진", example = "https://lh3.googleusercontent.com/a/ACg8ocI-Fz8cwMOHu2AJttFMt-s-25lPa--EUakuTRgvqfi-bA=s96-c")
    private String profile;

    @Schema(description = "사용자 한줄소개")
    private String bio;
}
