package com.m9d.sroom.util;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.m9d.sroom.member.domain.Member;

import java.util.UUID;

public class ServiceTest extends SroomTest{

    protected Member getNewMember() {
        UUID uuid = UUID.randomUUID();

        String memberCode = uuid.toString();
        return memberService.findOrCreateMemberByMemberCode(memberCode);
    }

}
