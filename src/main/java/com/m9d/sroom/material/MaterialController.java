package com.m9d.sroom.material;

import com.m9d.sroom.common.entity.jpa.CourseEntity;
import com.m9d.sroom.common.entity.jpa.CourseVideoEntity;
import com.m9d.sroom.common.entity.jpa.MemberEntity;
import com.m9d.sroom.course.CourseServiceHelper;
import com.m9d.sroom.course.CourseServiceVJpa;
import com.m9d.sroom.material.dto.request.FeedbackRequest;
import com.m9d.sroom.material.dto.request.SubmittedQuizRequest;
import com.m9d.sroom.material.dto.request.SummaryEditRequest;
import com.m9d.sroom.material.dto.response.*;
import com.m9d.sroom.material.exception.MaterialTypeNotFoundException;
import com.m9d.sroom.material.model.MaterialType;
import com.m9d.sroom.member.MemberServiceVJpa;
import com.m9d.sroom.quiz.QuizService;
import com.m9d.sroom.quiz.QuizServiceVJpa;
import com.m9d.sroom.summary.SummaryService;
import com.m9d.sroom.summary.SummaryServiceVJpa;
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
@Slf4j
public class MaterialController {

    private final JwtUtil jwtUtil;
    private final MaterialServiceVJpa materialService;
    private final FeedbackServiceVJpa feedbackService;
    private final MemberServiceVJpa memberService;
    private final CourseServiceVJpa courseService;
    private final SummaryServiceVJpa summaryService;
    private final QuizServiceVJpa quizService;

    @Auth
    @GetMapping("/materials/{courseVideoId}")
    @Tag(name = "강의 수강")
    @Operation(summary = "강의자료 불러오기", description = "영상 ID를 사용해 저장된 강의노트, 퀴즈를 불러옵니다.")
    @ApiResponse(responseCode = "200", description = "성공적으로 강의 자료를 불러왔습니다.", content = @Content(schema = @Schema(implementation = Material.class)))
    public Material getMaterials(@PathVariable("courseVideoId") Long courseVideoId) {
        MemberEntity memberEntity = memberService.getMemberEntity(jwtUtil.getMemberIdFromRequest());
        return materialService.getMaterials(courseService.getCourseVideoEntity(memberEntity, courseVideoId));
    }

    @Auth
    @PutMapping("/materials/summaries/{courseVideoId}")
    @Tag(name = "강의 수강")
    @Operation(summary = "강의 노트 수정하기", description = "영상 ID를 사용해 저장된 강의노트를 수정합니다.")
    @ApiResponse(responseCode = "200", description = "성공적으로 강의 노트를 업데이트 했습니다.", content = @Content(schema = @Schema(implementation = SummaryId.class)))
    public SummaryId updateSummaries(@PathVariable("courseVideoId") Long courseVideoId, @RequestBody SummaryEditRequest summaryEdit) {
        MemberEntity memberEntity = memberService.getMemberEntity(jwtUtil.getMemberIdFromRequest());

        return summaryService.update(courseService.getCourseVideoEntity(memberEntity, courseVideoId), summaryEdit.getContent());
    }

    @Auth
    @PostMapping("/materials/quizzes/{courseVideoId}")
    @Tag(name = "강의 수강")
    @Operation(summary = "퀴즈 채점 결과 저장", description = "courseVideoId 를 받아 채점 결과를 courseQuiz 테이블에 저장합니다.")
    @ApiResponse(responseCode = "200", description = "성공적으로 채점 결과를 저장했습니다.", content = @Content(schema = @Schema(implementation = SubmittedQuizInfoResponse.class)))
    public List<SubmittedQuizInfoResponse> submitQuizResults(@PathVariable("courseVideoId") Long courseVideoId, @RequestBody List<SubmittedQuizRequest> submittedQuizList) {
        MemberEntity memberEntity = memberService.getMemberEntity(jwtUtil.getMemberIdFromRequest());
        return quizService.submit(courseService.getCourseVideoEntity(memberEntity, courseVideoId), submittedQuizList);
    }

    @Auth
    @PutMapping("/materials/quizzes/{courseQuizId}/scrap")
    @Tag(name = "강의 수강")
    @Operation(summary = "퀴즈 오답노트 등록, 취소", description = "courseQuizId 를 받아 해당 퀴즈를 오답노트에 등록하거나 취소합니다.")
    @ApiResponse(responseCode = "200", description = "성공적으로 오답노트에 등록/취소하였습니다.", content = @Content(schema = @Schema(implementation = ScrapResult.class)))
    public ScrapResult switchScrapFlag(@PathVariable("courseQuizId") Long courseQuizId) {
        MemberEntity memberEntity = memberService.getMemberEntity(jwtUtil.getMemberIdFromRequest());

        return quizService.switchScrapFlag(memberEntity, courseQuizId);
    }

    @Auth
    @GetMapping("/courses/materials/{courseId}")
    @Tag(name = "내 강의실")
    @Operation(summary = "pdf 변환을 위한 강의자료 불러오기", description = "코스의 모든 강의자료를 pdf 변환하기 위해 해당 자료를 불러옵니다.")
    @ApiResponse(responseCode = "200", description = "성공적으로 강의자료를 불러왔습니다.", content = @Content(schema = @Schema(implementation = Material4PdfResponse.class)))
    public Material4PdfResponse getMaterialsForConvertingPdf(@PathVariable("courseId") Long courseId) {
        MemberEntity memberEntity = memberService.getMemberEntity(jwtUtil.getMemberIdFromRequest());

        return materialService.getCourseMaterials(courseService.getCourseEntity(memberEntity, courseId));
    }

    @Auth
    @PostMapping("/materials/{materialId}/feedback")
    @Tag(name = "수강 페이지")
    @Operation(summary = "강의자료 사용자 피드백", description = "강의노트와 퀴즈에 대한 사용자 피드백을 저장합니다.")
    @ApiResponse(responseCode = "200", description = "성공적으로 강의자료 피드백을 저장했습니다.", content = @Content(schema = @Schema(implementation = FeedbackInfo.class)))
    public FeedbackInfo feedbackMaterial(@PathVariable("materialId") Long materialId,
                                         @RequestParam(value = "type") String materialType,
                                         @RequestBody FeedbackRequest feedbackRequest) {
        MemberEntity memberEntity = memberService.getMemberEntity(jwtUtil.getMemberIdFromRequest());

        return feedbackService.feedback(memberEntity, MaterialType.fromStr(materialType),
                materialId, feedbackRequest.isSatisfactory());
    }
}
