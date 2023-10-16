package com.m9d.sroom.common.repository.member;

import com.m9d.sroom.common.dto.Member;

import java.util.Optional;

public interface MemberRepository {

    Member save(Member member);

    Member getById(Long memberId);

    Member getByCode(String memberCode);

    Optional<Member> findByCode(String memberCode);

    Optional<Member> findById(Long memberId);

    Member updateById(Long memberId, Member member);

    Integer countCompletedCourseById(Long memberId);

    Integer countCourseById(Long memberId);
}
