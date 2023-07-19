package com.m9d.sroom.course.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "강의 코스 목록 조회 정보")
@Data
public class CourseInfo {

    @Schema(description = "코스 ID")
    private Long courseId;

    @Schema(description = "코스 제목 - 첫 영상 제목")
    private String courseTitle;

    @Schema(description = "채널 리스트")
    private String[] channels;

    @Schema(description = "코스 내 영상 갯수")
    private int lectureCount;

    @Schema(description = "코스 총 재생 시간")
    private String courseDuration;

    @Schema(description = "수강률")
    private float progress;

    @Schema(description = "마지막 수강 시간")
    private String lastViewTime;

    public String[] getChannels() {
        return channels;
    }
}
