package com.m9d.sroom.member.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "구글 ID 토큰")
@Data
@NoArgsConstructor
public class GoogleIdKey {

    @Schema(description = "구글 ID 토큰", example = "eyJhbGciOiJSUzI1NiIsImtpZCI6Ijk5YmNiMDY5MzQwYTNmMTQ3NDYyNzk0ZGZlZmE3NWU3OTk2MTM2MzQiLCJ0eXAiOiJKV1QifQ.eyJpc3MiOiJodHRwczovL2FjY291bnRzLmdvb2dsZS5jb20iLCJuYmYiOjE2ODgyMTkyODAsImF1ZCI6IjEwODUyMjEwNDc5NDYtNHMxZ2YwZjlqdHQ3OTB2a2ZkcHZ1Z3FqbW9qMzdrdGsuYXBwcy5nb29nbGV1c2VyY29udGVudC5jb20iLCJzdWIiOiIxMDYwMzMxMTQ0MzU1MjY2MTA0ODciLCJlbWFpbCI6ImxqanVuMDcwMkBnbWFpbC5jA0Nzk0Ni00czFnZjBmOWp0dDc5MHZrZmRwdnVncWptb2ozN2t0ay5hcHBzLmdvb2dsZXVzZXJjb250ZW50LmNvbSIsIm5hbWUiOiLsnbTsooXspIAiLCJwaWN0dXJlIjoiaHR0cHM6Ly9saDMuZ29vZ2xldXNlcmNvbnRlbnQuY29tL2EvQUF=NLQmhRemdtZnNMdXpJVEhNS081bHNnN3FrPXM5Ni1jIiwiZ2l2ZW5fbmFtZSI6IuyiheykgCIsImZhbWlseV9uYW1lIjoi7J20IiwiaWF0IjoxNjg4MjE5NTgwLCJleHAiOjE2ODgyMjMxODAsImp0aSI6kNmVmNmMyM2JlZDgifQ.U1zcrnD9T9tnq2boIRZ9m37fVqeq25elAyBtghKG1p4mzbI1VoygbzdbyYihUkIVz8I21EDiiny9pO8ZBh7_pfSVHXok2XWPtWtzp3SHFKwePhW-SD0HAh8vDPJrkGCiQj5CupAL5nIs-GlIfusC7rx9A1hHaxu-CNyuEDlqrHh-MHlQLN4Wp42RfptOf68cIZZvKQ9D-Fx7Ag6F3Ot8Wfq5yZA")
    private String credential;

    @Builder
    public GoogleIdKey(String credential) {
        this.credential = credential;
    }
}
