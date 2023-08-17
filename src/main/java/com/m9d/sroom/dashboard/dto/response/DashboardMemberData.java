package com.m9d.sroom.dashboard.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
@Schema(description = "멤버 테이블에서 불러올 대시보드 데이터")
public class DashboardMemberData {

    @Schema(description = "총 푼 퀴즈 수")
    private int totalSolvedCount;

    @Schema(description = "총 퀴즈 정답 수")
    private int totalCorrectCount;

    @Schema(description = "총 누적 수강 시간")
    private int totalLearningTime;

    @Schema(description = "완강률")
    private int completionRate;
}
