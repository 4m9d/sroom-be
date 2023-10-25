package com.m9d.sroom.dashboard.dto.response;

import com.m9d.sroom.course.dto.response.CourseInfo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Schema(description = "대시보드 반환 데이터")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Dashboard {

    @Schema(description = "퀴즈 정답률")
    private int correctnessRate;

    @Schema(description = "완강률")
    private int completionRate;

    @Schema(description = "전체 누적수강 시간")
    private int totalLearningTime;

    @Schema(description = "동기부여 문구")
    private String motivation;

    @Schema(description = "최근 수강 강의")
    private List<CourseInfo> latestLectures;

    @Schema(description = "일별 수강 로그")
    private List<LearningHistory> learningHistories;

    @Schema(description = "틀린 퀴즈 리스트")
    private List<DashboardQuizData> wrongQuizzes;
}
