package com.m9d.sroom.util;

import com.m9d.sroom.member.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class ServiceTest {

    @Autowired
    protected MemberRepository memberRepository;
}
