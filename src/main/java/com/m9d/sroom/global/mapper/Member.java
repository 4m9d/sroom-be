package com.m9d.sroom.global.mapper;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Getter
@Builder
public class Member {

    @Id
    private Long memberId;

    @Setter
    private String memberCode;

    @Setter
    private String memberName;

    @Setter
    private String bio;

    @Setter
    private String refreshToken;

    @Setter
    private Integer totalSolvedCount;

    @Setter
    private Integer totalCorrectCount;

    @Setter
    private Integer completionRate;

    @Setter
    private Integer totalLearningTime;

    private Timestamp signUpTime;

    @Setter
    private Integer status;
}
