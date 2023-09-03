package com.m9d.sroom.material.controller;

import com.m9d.sroom.material.dto.request.CourseId;
import com.m9d.sroom.material.dto.request.SummaryEdit;
import com.m9d.sroom.material.dto.response.Material;
import com.m9d.sroom.material.dto.response.SummaryId;
import com.m9d.sroom.material.service.MaterialService;
import com.m9d.sroom.util.JwtUtil;
import com.m9d.sroom.util.annotation.Auth;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Slf4j
public class MaterialController {

    private final JwtUtil jwtUtil;
    private final MaterialService materialService;

    @Auth
    @GetMapping("/materials/lectures/{courseVideoId}")
    @Tag(name = "강의 수강")
    @Operation(summary = "강의자료 불러오기", description = "영상 ID를 사용해 저장된 강의노트, 퀴즈를 불러옵니다.")
    @ApiResponse(responseCode = "200", description = "성공적으로 강의 자료를 불러왔습니다.", content = @Content(schema = @Schema(implementation = Material.class)))
    public Material getMaterials(@PathVariable("courseVideoId") Long courseVideoId) {
        Long memberId = jwtUtil.getMemberIdFromRequest();
        return materialService.getMaterials(memberId, courseVideoId);
    }

    @Auth
    @PutMapping("/materials/lectures/{courseVideoId}")
    @Tag(name = "강의 수강")
    @Operation(summary = "강의 노트 수정하기", description = "영상 ID를 사용해 저장된 강의노트를 수정합니다.")
    @ApiResponse(responseCode = "200", description = "성공적으로 강의 노트를 업데이트 했습니다.")
    public SummaryId updateSummaries(@PathVariable("courseVideoId") Long courseVideoId, @RequestBody SummaryEdit summaryEdit) {
        Long memberId = jwtUtil.getMemberIdFromRequest();
        return materialService.updateSummary(memberId, courseVideoId, summaryEdit.getContent());
    }
}
