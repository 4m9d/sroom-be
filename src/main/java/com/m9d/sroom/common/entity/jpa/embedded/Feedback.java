package com.m9d.sroom.common.entity.jpa.embedded;

import lombok.*;

import javax.persistence.Embeddable;

@Embeddable
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Setter
public class Feedback {

    private Integer positiveFeedbackCount;

    private Integer negativeFeedbackCount;
}