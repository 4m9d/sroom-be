package com.m9d.sroom.common.entity.jpa;

import javax.persistence.*;

@Entity
@Table(name = "RECOMMEND")
public class RecommendEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long recommendId;

    private String sourceCode;

    private Boolean isPlaylist;

    private Integer domain;
}
