package com.m9d.sroom.dashbord.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "최근 수강 강의")
@Data
public class LatestLecture {

    @Schema(description = "코스 id")
    private long courseId;

    @Schema(description = "채널명")
    private String channel;

    @Schema(description = "썸네일 주소")
    private String thumbnail;

    @Schema(description = "코스 제목")
    private String courseTitle;

    @Schema(description = "코스 총 길이")
    private int courseDuration;

    @Schema(description = "마지막 수강 시간")
    private String lastViewTime;

    @Schema(description = "진도율")
    private float progress;
}
