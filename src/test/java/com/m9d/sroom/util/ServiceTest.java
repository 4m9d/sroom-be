package com.m9d.sroom.util;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.m9d.sroom.member.domain.Member;

import java.util.UUID;

public class ServiceTest extends SroomTest{

    protected GoogleIdToken.Payload getGoogleIdTokenPayload() {
        GoogleIdToken.Payload payload = new GoogleIdToken.Payload();

        // Generate a UUID
        UUID uuid = UUID.randomUUID();

        // Convert it to a string
        String randomUUIDString = uuid.toString();
        String expectedMemberCode = randomUUIDString;
        payload.setSubject(expectedMemberCode);

        return payload;
    }

    protected Member getNewMember() {
        String memberCode = memberService.getMemberCodeFromPayload(getGoogleIdTokenPayload());
        return memberService.findOrCreateMemberByMemberCode(memberCode);
    }

}
