package com.m9d.sroom.dashboard.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
@Schema(description = "대시보드 틀린 퀴즈 목록 데이터")
public class DashboardQuizData {

    @Schema(description = "퀴즈 문제")
    private String quizQuestion;

    @Schema(description = "퀴즈 정답")
    private String quizAnswer;

    @Schema(description = "영상 제목")
    private String videoTitle;

    @Schema(description = "퀴즈 푼 날짜")
    private String submittedAt;
}
