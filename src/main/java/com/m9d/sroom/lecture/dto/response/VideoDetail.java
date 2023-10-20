package com.m9d.sroom.lecture.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.m9d.sroom.common.vo.Content;
import com.m9d.sroom.common.vo.Video;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.util.HtmlUtils;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;

@Schema(description = "동영상 상세 정보")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VideoDetail {

    @Schema(description = "강의 ID", example = "OEV8gMkCHXQ")
    private String lectureCode;

    @Schema(description = "강의 제목", example = "네트워크 기초(개정판)")
    private String lectureTitle;

    @Schema(description = "채널 이름", example = "OpenAI")
    private String channel;

    @Schema(description = "강의 설명", example = "OSI 7계층에서 각 계층의 다양한 프로토콜들을 통해서 배우는 네트워크 기초에 대한 강의입니다.")
    private String description;

    @Schema(description = "강의 재생 길이", example = "11424")
    private int duration;

    @Schema(description = "강의 등록 여부", example = "false")
    @JsonProperty("is_enrolled")
    private boolean enrolled;

    @Schema(description = "플레이리스트 여부", example = "false")
    @JsonProperty("is_playlist")
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

    @Schema(description = "멤버의 코스 리스트")
    private List<CourseBrief> courses;

    @Schema(description = "회원 전용 강의 표시", example = "false")
    @JsonProperty("is_members_only")
    private boolean membership;

    @Schema(description = "목차 정보")
    private IndexInfo indexes;

    public VideoDetail(Video video, Set<String> enrolledLectureSet, List<CourseBrief> courseBriefList,
                       List<ReviewBrief> reviewList) {
        this.lectureCode = video.getCode();
        this.lectureTitle = HtmlUtils.htmlUnescape(video.getTitle());
        this.channel = video.getChannel();
        this.description = HtmlUtils.htmlUnescape(video.getDescription());
        this.duration = video.getDuration();
        this.playlist = false;
        this.enrolled = enrolledLectureSet.contains(video.getCode());
        this.publishedAt = video.getPublishedAt().toLocalDateTime().toLocalDate().format(DateTimeFormatter.ISO_LOCAL_DATE);
        this.thumbnail = video.getThumbnail();
        this.reviews = reviewList;
        this.reviewCount = reviewList.size();
        if (reviewList == null || reviews.isEmpty()) {
            this.rating = 0.0;
        } else {
            this.rating = reviews.stream()
                    .mapToInt(ReviewBrief::getSubmittedRating)
                    .average()
                    .orElse(0.0);
        }
        this.courses = courseBriefList;
        this.viewCount = video.getViewCount();
        this.membership = video.getMembership();
        this.indexes = IndexInfo.builder().
                indexList(List.of(new Index(0, video.getThumbnail(), video.getTitle(), video.getDuration(),
                        video.getMembership())))
                .build();
    }
}
