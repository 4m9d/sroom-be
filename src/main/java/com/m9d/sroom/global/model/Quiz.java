package com.m9d.sroom.global.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Quiz {

    private Long id;

    private Long videoId;

    private int type;

    private String question;

    private String subjectiveAnswer;

    private Integer choiceAnswer;
}
