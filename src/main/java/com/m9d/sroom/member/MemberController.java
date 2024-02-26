package com.m9d.sroom.member;

import com.m9d.sroom.member.dto.request.GoogleIdKey;
import com.m9d.sroom.member.dto.request.NameUpdateRequest;
import com.m9d.sroom.member.dto.request.RefreshToken;
import com.m9d.sroom.member.dto.response.Login;
import com.m9d.sroom.member.dto.response.NameUpdateResponse;
import com.m9d.sroom.util.JwtUtil;
import com.m9d.sroom.util.annotation.Auth;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/members")
@Slf4j
public class MemberController {

    private final MemberServiceVJpa memberService;
    private final JwtUtil jwtUtil;

    @PostMapping("/login")
    @Tag(name = "로그인")
    @Operation(summary = "사용자 로그인", description = "구글 ID 키를 사용하여 사용자를 인증합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공적으로 사용자를 인증하였습니다.", content = {@Content(mediaType = "application/json", schema = @Schema(allOf = Login.class))}),
            @ApiResponse(responseCode = "400", description = "요청 본문이 유효하지 않습니다.", content = {@Content}),
            @ApiResponse(responseCode = "401", description = "인증에 실패하였습니다.", content = @Content)
    })
    public Login login(@RequestBody GoogleIdKey googleIdKey) throws Exception {
        log.info("member login request. credential = {}", googleIdKey.getCredential());
        return memberService.authenticateMember(googleIdKey.getCredential());
    }

    @PostMapping("/refresh")
    @Tag(name = "로그인")
    @Operation(summary = "access token 갱신", description = "refresh token을 사용하여 로그인을 유지합니다.")
    @ApiResponse(responseCode = "200", description = "토큰 갱신에 성공하였습니다.", content = {@Content(mediaType = "application/json", schema = @Schema(allOf = Login.class))})
    public Login refresh(@RequestBody RefreshToken refreshToken) {
        return memberService.verifyRefreshToken(refreshToken.getRefreshToken());
    }

    @Auth
    @PutMapping("/profile")
    @Tag(name = "로그인")
    @Operation(summary = "프로필 이름 수정", description = "name을 받아 멤버의 이름을 수정합니다.")
    @ApiResponse(responseCode = "200", description = "멤버 이름 변경에 성공하였습니다.", content = {@Content(mediaType = "application/json", schema = @Schema(allOf = NameUpdateResponse.class))})
    public NameUpdateResponse updateMemberName(@RequestBody NameUpdateRequest nameUpdateRequest) {
        Long memberId = jwtUtil.getMemberIdFromRequest();
        return memberService.updateMemberName(memberId, nameUpdateRequest.getName());
    }
}
