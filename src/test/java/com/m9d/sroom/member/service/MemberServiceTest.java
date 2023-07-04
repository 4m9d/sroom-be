package com.m9d.sroom.member.service;

import com.m9d.sroom.util.ServiceTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class MemberServiceTest extends ServiceTest {

    @Autowired
    private MemberService memberService;

}
