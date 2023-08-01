package com.m9d.sroom.dashbord.controller;

import com.m9d.sroom.dashbord.dto.response.Dashboard;
import com.m9d.sroom.dashbord.service.DashboardService;
import com.m9d.sroom.util.JwtUtil;
import com.m9d.sroom.util.annotation.Auth;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/dashboards")
@Slf4j
public class DashboardController {

    private final DashboardService dashboardService;
    private final JwtUtil jwtUtil;

    @Auth
    @GetMapping("")
    @Tag(name = "대시보드")
    @Operation(summary = "대시보드 불러오기", description = "유저 ID 바탕으로 대시보드 정보 불러오기")
    @ApiResponse(responseCode = "200", description = "성공적으로 대시보드를 반환하였습니다.", content = @Content(schema = @Schema(implementation = Dashboard.class)))
    public Dashboard getDashboard() {
        Long memberId = jwtUtil.getMemberIdFromRequest();
        Dashboard dashboardData = dashboardService.getDashboard(memberId);
        return dashboardData;
    }
}
