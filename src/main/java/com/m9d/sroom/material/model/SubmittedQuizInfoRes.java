package com.m9d.sroom.material.model;

import lombok.Builder;
import lombok.Data;

import java.sql.Timestamp;

@Data
@Builder
public class SubmittedQuizInfoRes {

    private String submittedAnswer;

    private boolean correct;

    private Timestamp submittedTime;

    private boolean scrapped;
}
