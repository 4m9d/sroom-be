package com.m9d.sroom.common.entity.jpa.embedded;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;

@Embeddable
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Feedback {

    private Integer positiveFeedbackCount;

    private Integer negativeFeedbackCount;
}