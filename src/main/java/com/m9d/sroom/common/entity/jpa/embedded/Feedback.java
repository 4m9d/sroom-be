package com.m9d.sroom.common.entity.jpa.embedded;

import lombok.Data;

import javax.persistence.Embeddable;

@Embeddable
@Data
public class Feedback {

    private Integer positiveFeedbackCount;

    private Integer negativeFeedbackCount;
}