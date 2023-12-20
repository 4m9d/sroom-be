package com.m9d.sroom.common.entity.jpa.embedded;

import lombok.*;

import javax.persistence.Embeddable;

@Embeddable
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Setter
public class Sequence {

    private Integer section;

    private Integer videoIndex;

    private Integer lectureIndex;
}
