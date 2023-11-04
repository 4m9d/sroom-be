package com.m9d.sroom.material.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Answer4PdfResponse {
    private int videoIndex;
    private int quizIndex;
    private String answer;
    private String answerStr;
}
