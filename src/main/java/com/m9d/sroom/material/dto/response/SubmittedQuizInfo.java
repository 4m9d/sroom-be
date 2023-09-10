package com.m9d.sroom.material.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class SubmittedQuizInfo {

    private Long id;

    private Long courseQuizId;
}
