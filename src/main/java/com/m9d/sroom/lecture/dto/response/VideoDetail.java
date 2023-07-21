package com.m9d.sroom.lecture.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Schema(description = "동영상 상세 정보")
@Data
@Builder
public class VideoDetail {

    @Schema(description = "강의 ID", example = "OEV8gMkCHXQ")
    private String lectureCode;

    @Schema(description = "강의 제목", example = "네트워크 기초(개정판)")
    private String lectureTitle;

    @Schema(description = "채널 이름", example = "OpenAI")
    private String channel;

    @Schema(description = "강의 설명", example = "OSI 7계층에서 각 계층의 다양한 프로토콜들을 통해서 배우는 네트워크 기초에 대한 강의입니다.")
    private String description;

    @Schema(description = "강의 재생 길이", example = "3:45")
    private String duration;

    @Schema(description = "강의 등록 여부", example = "false")
    private boolean enrolled;

    @Schema(description = "플레이리스트 여부", example = "false")
    private boolean playlist;

    @Schema(description = "조회 수", example = "10234")
    private long viewCount;

    @Schema(description = "게시일", example = "2023-06-28")
    private String publishedAt;

    @Schema(description = "강의 평점", example = "4.3")
    private double rating;

    @Schema(description = "후기 개수", example = "44")
    private int reviewCount;

    @Schema(description = "강의 썸네일", example = "https://i.ytimg.com/vi/OEV8gMkCHXQ/maxresdefault.jpg")
    private String thumbnail;

    @Schema(description = "강의 리뷰 목록")
    private List<ReviewBrief> reviews;
}
