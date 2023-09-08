package com.m9d.sroom.material.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@Builder
@AllArgsConstructor
public class SubmittedQuizInfo {

    private Long quizId;

    private Long courseQuizId;
}
