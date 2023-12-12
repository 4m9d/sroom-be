package com.m9d.sroom.common.entity.jpa.embedded;

import javax.persistence.Embeddable;

@Embeddable
public class Feedback {

    private Integer positiveFeedbackCount;

    private Integer negativeFeedbackCount;
}