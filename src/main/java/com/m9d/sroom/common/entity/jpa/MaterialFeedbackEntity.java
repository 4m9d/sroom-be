package com.m9d.sroom.common.entity.jpa;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "MATERIAL_FEEDBACK")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
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

    private MaterialFeedbackEntity(MemberEntity member, Long contentId, Integer contentType, Boolean rating) {
        this.contentId = contentId;
        this.contentType = contentType;
        this.rating = rating;
        setMember(member);
    }

    private void setMember(MemberEntity member) {
        if (this.member != null) {
            member.getFeedbacks().remove(this);
        }

        this.member = member;
        member.getFeedbacks().add(this);
    }

    public static MaterialFeedbackEntity create(MemberEntity member, Long contentId, int contentType, boolean rating) {
        return new MaterialFeedbackEntity(member, contentId, contentType, rating);
    }
}
