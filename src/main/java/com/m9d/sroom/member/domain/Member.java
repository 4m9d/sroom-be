package com.m9d.sroom.member.domain;

import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;

@Getter
public class Member {

    @Id
    private Long memberId;
    private String memberCode;
    private String memberName;
    private String bio;
    private String refreshToken;
    private LocalDateTime signUpTime;
    private int status;

    @Builder
    public Member(Long memberId, String memberCode, String memberName, String bio) {
        this.memberId = memberId;
        this.memberCode = memberCode;
        this.memberName = memberName;
        this.bio = bio;
    }
}
