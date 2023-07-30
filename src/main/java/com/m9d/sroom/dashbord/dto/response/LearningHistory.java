package com.m9d.sroom.dashbord.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Builder
@Schema(description = "일일 수강 로그")
@Data
public class LearningHistory {

    @Schema(description = "날짜")
    private String date;

    @Schema(description = "수강 시간")
    private int learningTime;

    @Schema(description = "일일 퀴즈 푼 갯수")
    private int quizCount;

    @Schema(description = "일일 수강 강의 갯수")
    private int lectureCount;

}
