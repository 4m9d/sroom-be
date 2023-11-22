package com.m9d.sroom.material.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
public class Answer4PdfResponse {

    private int videoIndex;
    private String videoTitle;

    private List<VideoAnswer4PdfResponse> videoAnswers;

    public static Answer4PdfResponse getDefault(int videoIndex, String title) {
        return new Answer4PdfResponse(videoIndex, title, new ArrayList<>());
    }
}
