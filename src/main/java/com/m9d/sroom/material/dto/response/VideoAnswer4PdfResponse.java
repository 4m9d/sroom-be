package com.m9d.sroom.material.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class VideoAnswer4PdfResponse {

    private int quizIndex;
    private String answer;
    private String answerStr;
}
