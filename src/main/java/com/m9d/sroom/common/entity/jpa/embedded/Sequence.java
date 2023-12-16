package com.m9d.sroom.common.entity.jpa.embedded;

import lombok.Data;

import javax.persistence.Embeddable;

@Embeddable
@Data
public class Sequence {

    private Integer section;

    private Integer videoIndex;

    private Integer lectureIndex;
}
