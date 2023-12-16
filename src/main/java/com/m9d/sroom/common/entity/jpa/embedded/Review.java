package com.m9d.sroom.common.entity.jpa.embedded;

import lombok.Data;

import javax.persistence.Embeddable;

@Embeddable
@Data
public class Review {

    private Integer accumulatedRating;

    private Integer reviewCount;

    private Double averageRating;
}
