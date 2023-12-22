package com.m9d.sroom.common.entity.jpa.embedded;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Embeddable;
import java.sql.Timestamp;

@Embeddable
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Grading {

    private String submittedAnswer;

    private Boolean isCorrect;

    //@CreationTimestamp
    @Setter
    private Timestamp submittedTime;
}
