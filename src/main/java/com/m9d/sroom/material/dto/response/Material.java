package com.m9d.sroom.material.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class Material {

    private int status;

    private int totalQuizCount;

    private List<Quiz> quizzes;

    private SummaryBrief summaryBrief;
}
