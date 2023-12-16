package com.m9d.sroom.common.entity.jpa.embedded;

import lombok.Data;

import javax.persistence.Embeddable;

@Embeddable
@Data
public class MemberStats {

    private Integer totalSolvedCount;

    private Integer totalCorrectCount;

    private Integer completionRate;

    private Integer totalLearningTime;
}
