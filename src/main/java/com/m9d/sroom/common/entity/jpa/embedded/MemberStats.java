package com.m9d.sroom.common.entity.jpa.embedded;

import javax.persistence.Embeddable;

@Embeddable
public class MemberStats {

    private Integer totalSolvedCount;

    private Integer totalCorrectCount;

    private Integer completionRate;

    private Integer totalLearningTime;
}
