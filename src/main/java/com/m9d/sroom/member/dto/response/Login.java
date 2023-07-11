package com.m9d.sroom.member.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Schema(description = "로그인 정보")
@Data
public class Login {

    @Schema(description = "엑세스 토큰", example = "eyJhbGci...")
    private String accessToken;

    @Schema(description = "리프레시 토큰", example = "eyJhbGci...")
    private String refreshToken;

    @Schema(description = "만료 시간(초)", example = "1688398507")
    private Long expireAt;

    @Schema(description = "멤버 이름", example = "user_978538")
    private String name;

    @Schema(description = "사용자 한줄소개")
    private String bio;

    @Builder
    public Login(String accessToken, String refreshToken, Long expireAt, String name, String bio) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.expireAt = expireAt;
        this.name = name;
        this.bio = bio;
    }
}
