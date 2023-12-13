package com.m9d.sroom.common.entity.jpa.embedded;

import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.Embeddable;
import java.sql.Timestamp;

@Embeddable
public class Grading {

    private String submittedAnswer;

    private Boolean isCorrect;

    //@CreationTimestamp
    @Setter
    private Timestamp submittedTime;
}
