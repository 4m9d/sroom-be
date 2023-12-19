package com.m9d.sroom.common.entity.jpa.embedded;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Embeddable;
import java.sql.Timestamp;

@Embeddable
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class LearningStatus {

    private Integer startTime;

    private Boolean isComplete;

    //@UpdateTimestamp
    @Setter
    private Timestamp lastViewTime;

    private Integer maxDuration;
}
