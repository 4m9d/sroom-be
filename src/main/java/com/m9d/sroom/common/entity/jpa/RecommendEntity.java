package com.m9d.sroom.common.entity.jpa;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;

import javax.persistence.*;

@Entity
@Table(name = "RECOMMEND")
@Getter
@NoArgsConstructor
@DynamicInsert
public class RecommendEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long recommendId;

    private String sourceCode;

    private Boolean isPlaylist;

    private Integer domain;
}
