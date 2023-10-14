package com.m9d.sroom.member.exception;

import com.m9d.sroom.common.error.UnauthorizedException;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "유효하지 않은 구글 ID 토큰일 때 발생하는 exception입니다.")
public class CredentialUnauthorizedException extends UnauthorizedException {

    private static final String MESSAGE = "입력받은 credential 토큰이 유효하지 않습니다";

    public CredentialUnauthorizedException() {
        super(MESSAGE);
    }
}
