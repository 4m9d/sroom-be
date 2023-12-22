package com.m9d.sroom.common.entity.jpa;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

import static lombok.AccessLevel.PROTECTED;

@Entity
@Table(name = "RECOMMEND")
@Getter
@NoArgsConstructor(access = PROTECTED)
public class RecommendEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long recommendId;

    private String sourceCode;

    private Boolean isPlaylist;

    private Integer domain;
}
