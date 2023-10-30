package com.m9d.sroom.material.dto.response;

import com.m9d.sroom.material.model.MaterialStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class Material4PdfResponse {

    private String courseTitle;

    private int totalVideoCount;

    private int status;

    private List<Content4PdfResponse> materials;

    private List<Answer4PdfResponse> answers;

    public static Material4PdfResponse createUnprepared(String title, int videoCount) {
        return Material4PdfResponse.builder()
                .courseTitle(title)
                .totalVideoCount(videoCount)
                .status(MaterialStatus.CREATING.getValue())
                .build();
    }

    public static Material4PdfResponse createPrepared(String title, int videoCount,
                                                      List<Content4PdfResponse> contentList,
                                                      List<Answer4PdfResponse> answerList) {
        return Material4PdfResponse.builder()
                .courseTitle(title)
                .totalVideoCount(videoCount)
                .status(MaterialStatus.CREATED.getValue())
                .materials(contentList)
                .answers(answerList)
                .build();
    }
}
