package com.m9d.sroom.search.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Schema(description = "수강 페이지 사이드바에 쓰이는 비디오의 요약 정보")
@Data
@Builder
public class VideoWatchInfo {

    @Schema(description = "비디오 ID", example = "1111")
    private Long videoId;

    @Schema(description = "비디오의 인덱스", example = "1")
    private int videoIndex;

    @Schema(description = "비디오의 COURSEVIDEO 테이블 PK", example = "123")
    private Long courseVideoId;

    @Schema(description = "비디오가 제공되는 채널", example = "발라더 손경식")
    private String channel;

    @JsonProperty("is_completed")
    @Schema(description = "해당 비디오가 완료되었는지 여부", example = "false")
    private boolean completed;

    @Schema(description = "비디오의 제목", example = "자바 스프링 기초")
    private String videoTitle;

    @Schema(description = "비디오의 코드", example = "JAVA_SPRING_BASICS")
    private String videoCode;

    @Schema(description = "영상 시작시간", example = "11")
    private int lastViewDuration;

    @Schema(description = "영상 재생시간", example = "1000")
    private int videoDuration;
}
