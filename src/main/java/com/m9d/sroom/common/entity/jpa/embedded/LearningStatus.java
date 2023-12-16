package com.m9d.sroom.common.entity.jpa.embedded;

import lombok.Data;
import lombok.Setter;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.Embeddable;
import java.sql.Timestamp;

@Embeddable
@Data
public class LearningStatus {

    private Integer startTime;

    private Boolean isComplete;

    //@UpdateTimestamp
    @Setter
    private Timestamp lastViewTime;

    private Integer maxDuration;
}
