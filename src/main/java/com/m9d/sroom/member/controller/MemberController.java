package com.m9d.sroom.member.controller;
import com.m9d.sroom.member.dto.request.GoogleIdKey;
import com.m9d.sroom.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/members")
@Slf4j
public class MemberController {

    private final MemberService memberService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody GoogleIdKey request) throws Exception{
        return ResponseEntity.ok(memberService.authenticateUser(request.getClientId(), request.getGoogleIdKey()));
    }
}
