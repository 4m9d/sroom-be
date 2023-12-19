package com.m9d.sroom.common.entity.jpa.embedded;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;

@Embeddable
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Scheduling {

    private Boolean isScheduled;

    private Integer weeks;

    @Temporal(TemporalType.DATE)
    private Date expectedEndDate;

    private Integer dailyTargetTime;
}
