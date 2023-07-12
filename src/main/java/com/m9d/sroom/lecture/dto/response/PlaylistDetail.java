package com.m9d.sroom.lecture.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Schema(description = "재생목록 상세 정보")
@Data
public class PlaylistDetail {

    @Schema(description = "재생목록 ID", example = "PL0d8NnikouEWcF1jJueLdjRIC4HsUlULi")
    private String lectureCode;

    @Schema(description = "재생목록 제목", example = "네트워크 기초(개정판)")
    private String lectureTitle;

    @Schema(description = "채널 이름", example = "Network Academy")
    private String channel;

    @Schema(description = "재생목록 설명", example = "OSI 7계층에서 각 계층의 다양한 프로토콜들을 통해서 배우는 네트워크 기초에 대한 강의입니다.")
    private String description;

    @Schema(description = "강의 등록 여부", example = "false")
    private boolean isEnrolled;

    @Schema(description = "플레이리스트 여부", example = "true")
    @JsonProperty("isPlaylist")
    private boolean isPlaylist;

    @Schema(description = "재생목록 게시 날짜", example = "2022-12-31T23:59:59Z")
    private String publishedAt;

    @Schema(description = "강의 개수", example = "20")
    private int lectureCount;

    @Schema(description = "강의 평점", example = "4.3")
    private double rating;

    @Schema(description = "후기 개수", example = "44")
    private int reviewCount;

    @Schema(description = "재생목록 썸네일", example = "https://i.ytimg.com/vi/Av9UFzl_wis/hqdefault.jpg")
    private String thumbnail;

    @Schema(description = "강의 목차 정보")
    private IndexInfo indexInfo;

    @Schema(description = "강의 후기 요약 정보")
    private List<ReviewBrief> reviews;

    @Builder
    public PlaylistDetail(String lectureCode, String lectureTitle, String channel, String description, boolean isEnrolled, boolean isPlaylist, String publishedAt, int lectureCount, double rating, int reviewCount, String thumbnail, IndexInfo indexInfo, List<ReviewBrief> reviews) {
        this.lectureCode = lectureCode;
        this.lectureTitle = lectureTitle;
        this.channel = channel;
        this.description = description;
        this.isEnrolled = isEnrolled;
        this.isPlaylist = isPlaylist;
        this.publishedAt = publishedAt;
        this.lectureCount = lectureCount;
        this.rating = rating;
        this.reviewCount = reviewCount;
        this.thumbnail = thumbnail;
        this.indexInfo = indexInfo;
        this.reviews = reviews;
    }
}
