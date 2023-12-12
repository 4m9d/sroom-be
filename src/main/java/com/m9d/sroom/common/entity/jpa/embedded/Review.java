package com.m9d.sroom.common.entity.jpa.embedded;

import javax.persistence.Embeddable;

@Embeddable
public class Review {

    private Integer accumulatedRating;

    private Integer reviewCount;

    private Double averageRating;
}
