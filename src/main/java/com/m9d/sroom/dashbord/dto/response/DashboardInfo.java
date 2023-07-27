package com.m9d.sroom.dashbord.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Schema(description = "대시보드 반환 데이터")
@Data
public class DashboardInfo {

    @Schema(description = "퀴즈 정답률")
    private int correctnessRate;

    @Schema(description = "완강률")
    private int completionRate;

    @Schema(description = "전체 누적수강 시간")
    private int totalLearningTime;

    @Schema(description = "동기부여 문구")
    private String motivation;

    @Schema(description = "최근 수강 강의")
    private List<Latest> latest;

    @Schema(description = "일별 수강 로그")
    private List<LearningHistory> learningHistory;
}
