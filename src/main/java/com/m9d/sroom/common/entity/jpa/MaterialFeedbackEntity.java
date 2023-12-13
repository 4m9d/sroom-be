package com.m9d.sroom.common.entity.jpa;

import javax.persistence.*;

@Entity
@Table(name = "MATERIAL_FEEDBACK")
public class MaterialFeedbackEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long feedbackId;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private MemberEntity member;

    private Long contentId;

    private Integer contentType;

    private Boolean rating;
}
