package com.m9d.sroom.common.entity.jpa.embedded;

import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.Embeddable;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;

@Embeddable
public class Scheduling {

    private Boolean isScheduled;

    private Integer weeks;

    @Temporal(TemporalType.DATE)
    private Date expectedEndDate;

    private Integer dailyTargetTime;
}
