package com.m9d.sroom.material.dto.response;

import com.m9d.sroom.common.entity.VideoEntity;
import com.m9d.sroom.course.vo.CourseVideo;
import com.m9d.sroom.material.model.MaterialStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class Content4PdfResponse {

    private String videoTitle;

    private int index;

    private String channel;

    private boolean usable;

    private SummaryBrief summaryBrief;

    private List<Quiz4PdfResponse> quizzes;


    public static Content4PdfResponse create(VideoEntity videoEntity, CourseVideo courseVideo,
                                             SummaryBrief summaryBrief, List<Quiz4PdfResponse> quizzeList) {
        return Content4PdfResponse.builder()
                .videoTitle(videoEntity.getTitle())
                .index(courseVideo.getVideoIndex())
                .channel(videoEntity.getChannel())
                .usable(courseVideo.getMaterialStatus().equals(MaterialStatus.CREATED))
                .summaryBrief(summaryBrief)
                .quizzes(quizzeList)
                .build();
    }
}
