package com.m9d.sroom.common.entity.jpa;

import com.m9d.sroom.common.entity.jpa.embedded.MemberStats;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "MEMBER")
public class MemberEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long memberId;

    private String memberCode;

    private String memberName;

    private String refreshToken;

    @Embedded
    private MemberStats stats;

    @CreationTimestamp
    private Timestamp signUpTime;

    private Boolean status;

    private String bio;
}
