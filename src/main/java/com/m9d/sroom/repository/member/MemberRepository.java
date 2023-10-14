package com.m9d.sroom.repository.member;

import com.m9d.sroom.global.mapper.MemberDto;

import java.util.Optional;

public interface MemberRepository {

    MemberDto save(MemberDto memberDto);

    MemberDto getById(Long memberId);

    MemberDto getByCode(String memberCode);

    Optional<MemberDto> findByCode(String memberCode);

    Optional<MemberDto> findById(Long memberId);

    MemberDto updateById(Long memberId, MemberDto memberDto);

    Integer countCompletedCourseById(Long memberId);

    Integer countCourseById(Long memberId);
}
