package com.m9d.sroom.material;

import com.m9d.sroom.course.CourseServiceHelper;
import com.m9d.sroom.material.dto.request.SubmittedQuizRequest;
import com.m9d.sroom.material.dto.request.SummaryEditRequest;
import com.m9d.sroom.material.dto.response.Material;
import com.m9d.sroom.material.dto.response.ScrapResult;
import com.m9d.sroom.material.dto.response.SubmittedQuizInfoResponse;
import com.m9d.sroom.material.dto.response.SummaryId;
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

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/materials")
@Slf4j
public class MaterialControllerV2 {

    private final JwtUtil jwtUtil;
    private final MaterialServiceV2 materialService;
    private final CourseServiceHelper courseServiceHelper;

    @Auth
    @GetMapping("/{courseVideoId}")
    @Tag(name = "강의 수강")
    @Operation(summary = "강의자료 불러오기", description = "영상 ID를 사용해 저장된 강의노트, 퀴즈를 불러옵니다.")
    @ApiResponse(responseCode = "200", description = "성공적으로 강의 자료를 불러왔습니다.", content = @Content(schema = @Schema(implementation = Material.class)))
    public Material getMaterials(@PathVariable("courseVideoId") Long courseVideoId) {
        return materialService.getMaterials(jwtUtil.getMemberIdFromRequest(), courseVideoId);
    }

    @Auth
    @PutMapping("/summaries/{courseVideoId}")
    @Tag(name = "강의 수강")
    @Operation(summary = "강의 노트 수정하기", description = "영상 ID를 사용해 저장된 강의노트를 수정합니다.")
    @ApiResponse(responseCode = "200", description = "성공적으로 강의 노트를 업데이트 했습니다.", content = @Content(schema = @Schema(implementation = SummaryId.class)))
    public SummaryId updateSummaries(@PathVariable("courseVideoId") Long courseVideoId, @RequestBody SummaryEditRequest summaryEdit) {
        return materialService.updateSummary(jwtUtil.getMemberIdFromRequest(), courseVideoId, summaryEdit.getContent());
    }

    @Auth
    @PostMapping("/quizzes/{courseVideoId}")
    @Tag(name = "강의 수강")
    @Operation(summary = "퀴즈 채점 결과 저장", description = "courseVideoId 를 받아 채점 결과를 courseQuiz 테이블에 저장합니다.")
    @ApiResponse(responseCode = "200", description = "성공적으로 채점 결과를 저장했습니다.", content = @Content(schema = @Schema(implementation = SubmittedQuizInfoResponse.class)))
    public List<SubmittedQuizInfoResponse> submitQuizResults(@PathVariable("courseVideoId") Long courseVideoId, @RequestBody List<SubmittedQuizRequest> submittedQuizList) {
        Long memberId = jwtUtil.getMemberIdFromRequest();
        return materialService.submitQuizResults(memberId, courseVideoId, submittedQuizList);
    }

    @Auth
    @PutMapping("/quizzes/{courseQuizId}/scrap")
    @Tag(name = "강의 수강")
    @Operation(summary = "퀴즈 오답노트 등록, 취소", description = "courseQuizId 를 받아 해당 퀴즈를 오답노트에 등록하거나 취소합니다.")
    @ApiResponse(responseCode = "200", description = "성공적으로 오답노트에 등록/취소하였습니다.", content = @Content(schema = @Schema(implementation = ScrapResult.class)))
    public ScrapResult switchScrapFlag(@PathVariable("courseQuizId") Long courseQuizId) {
        Long memberId = jwtUtil.getMemberIdFromRequest();
        courseServiceHelper.validateCourseQuizForMember(memberId, courseQuizId);
        return materialService.switchScrapFlag(courseQuizId);
    }
}
