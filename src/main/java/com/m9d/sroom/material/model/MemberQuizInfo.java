package com.m9d.sroom.material.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MemberQuizInfo {

    private int TotalSolvedCount;

    private int totalCorrectCount;
}
