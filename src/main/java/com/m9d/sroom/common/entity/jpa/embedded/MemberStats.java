package com.m9d.sroom.common.entity.jpa.embedded;

import lombok.*;

import javax.persistence.Embeddable;

@Embeddable
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Setter
public class MemberStats {

    private Integer totalSolvedCount;

    private Integer totalCorrectCount;

    private Integer completionRate;

    private Integer totalLearningTime;
}
