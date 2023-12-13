package com.m9d.sroom.common.entity.jpa.embedded;

import javax.persistence.Embeddable;

@Embeddable
public class Sequence {

    private Integer section;

    private Integer videoIndex;

    private Integer lectureIndex;
}
