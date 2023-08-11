package com.m9d.sroom.course.dto.response;

import com.m9d.sroom.lecture.dto.response.LastVideoInfo;
import com.m9d.sroom.lecture.dto.response.Section;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Schema(description = "수강페이지에 쓰이는 강의에 대한 상세 정보")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourseDetail {

    @Schema(description = "강의의 고유 식별자", example = "102")
    private Long courseId;

    @Schema(description = "강의가 일정을 사용하는지 여부", example = "true")
    private boolean useSchedule;

    @Schema(description = "코스 썸네일", example = "https://i.ytimg.com/vi/c2cz_uF3NII/maxresdefault.jpg")
    private String thumbnail;

    @Schema(description = "강의의 제목", example = "자바 기초")
    private String courseTitle;

    @Schema(description = "강의가 제공되는 채널", example = "YouTube")
    private String channels;

    @Schema(description = "강의의 총 기간 (분 단위)", example = "120")
    private int courseDuration;

    @Schema(description = "현재 시청 완료된 강의 기간 (분 단위)", example = "60")
    private int currentDuration;

    @Schema(description = "강의의 총 비디오 개수", example = "10")
    private int totalVideoCount;

    @Schema(description = "강의에서 완료된 비디오 수", example = "5")
    private int completedVideoCount;

    @Schema(description = "강의 진행률 퍼센트", example = "50")
    private int progress;

    @Schema(description = "강의에서 마지막으로 시청한 비디오 정보")
    private LastVideoInfo lastViewVideo;

    @Schema(description = "강의 일정의 주 리스트")
    private List<Section> sections;
}
