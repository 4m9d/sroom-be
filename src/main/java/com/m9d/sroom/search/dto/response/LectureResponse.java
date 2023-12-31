package com.m9d.sroom.search.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.m9d.sroom.common.vo.Content;
import com.m9d.sroom.youtube.vo.SearchItemInfo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.util.HtmlUtils;

import java.text.SimpleDateFormat;

@Schema(description = "검색된 개별 강의 정보")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LectureResponse {

    @Schema(description = "강의 제목", example = "네트워크 기초(개정판)")
    private String lectureTitle;

    @Schema(description = "강의 설명", example = "OSI 7계층에서 각 계층의 다양한 프로토콜들을 통해서 배우는 네트워크 기초에 대한 강의입니다.")
    private String description;

    @Schema(description = "채널", example = "따라하면서 배우는 IT")
    private String channel;

    @Schema(description = "강의 등록 여부", example = "false")
    @JsonProperty("is_enrolled")
    private Boolean enrolled;

    @Schema(description = "강의 ID", example = "PL0d8NnikouEWcF1jJueLdjRIC4HsUlULi")
    private String lectureCode;

    @Schema(description = "플레이리스트 여부", example = "true")
    @JsonProperty("is_playlist")
    private Boolean isPlaylist;

    @Schema(description = "강의 평점", example = "4.3")
    private double rating;

    @Schema(description = "후기 개수", example = "44")
    private int reviewCount;

    @Schema(description = "강의 썸네일", example = "https://i.ytimg.com/vi/Av9UFzl_wis/hqdefault.jpg")
    private String thumbnail;

    @Schema(description = "강의 개수", example = "11")
    private int lectureCount;

    @Schema(description = "조회수", example = "1222222")
    private Long viewCount;

    @Schema(description = "생성일", example = "2022-05-23 10:30:21")
    private String publishedAt;

    public LectureResponse(SearchItemInfo item, Content content) {
        this.isPlaylist = item.isPlaylist();
        this.lectureTitle = HtmlUtils.htmlUnescape(item.getTitle());
        this.description = HtmlUtils.htmlUnescape(content.getDescription());
        this.channel = HtmlUtils.htmlUnescape(item.getChannel());
        this.lectureCode = item.getCode();
        this.enrolled = false;
        this.publishedAt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(item.getPublishedAt());
        this.lectureCount = content.getVideoCount();
        this.viewCount = content.getViewCount();
        this.thumbnail = content.getThumbnail();
        this.rating = content.getRating();
        this.reviewCount = content.getReviewCount();
    }
}
