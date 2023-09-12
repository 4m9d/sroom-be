package com.m9d.sroom.global.model;

import lombok.Builder;
import lombok.Data;

import java.sql.Timestamp;

@Data
@Builder
public class Review {

    private Long id;

    private Long memberId;

    private String sourceCode;

    private Integer submitted_rating;

    private String content;

    private Timestamp submittedDate;
}
