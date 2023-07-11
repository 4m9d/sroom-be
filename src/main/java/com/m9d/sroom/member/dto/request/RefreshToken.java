package com.m9d.sroom.member.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Schema(description = "리프레시 토큰")
public class RefreshToken {

    @Schema(description = "리프레시 토큰", example = "eyJhbGci...")
    private String refreshToken;

    @Builder
    public RefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}
