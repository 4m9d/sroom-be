package com.m9d.sroom.lecture.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Schema(description = "마지막으로 시청한 비디오에 대한 정보")
@Data
@Builder
public class LastVideoInfo {

    @Schema(description = "비디오의 고유 식별자", example = "20")
    private Long videoId;

    @Schema(description = "비디오의 COURSEVIDEO 테이블 PK", example = "11")
    private Long courseVideoId;

    @Schema(description = "비디오의 제목", example = "자바 스프링 기초")
    private String videoTitle;

    @Schema(description = "비디오의 코드", example = "JAVA_SPRING_BASICS")
    private String videoCode;

    @Schema(description = "비디오가 제공되는 채널", example = "래퍼 손경식")
    private String channel;

    @Schema(description = "마지막으로 시청한 비디오의 기간 (초 단위)", example = "30")
    private int lastViewDuration;

}
