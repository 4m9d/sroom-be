package com.m9d.sroom.common.repository.member;

import com.m9d.sroom.common.entity.jdbctemplate.MemberEntity;

import java.util.Optional;

public interface MemberRepository {

    MemberEntity save(MemberEntity member);

    MemberEntity getById(Long memberId);

    MemberEntity getByCode(String memberCode);

    Optional<MemberEntity> findByCode(String memberCode);

    Optional<MemberEntity> findById(Long memberId);

    MemberEntity updateById(Long memberId, MemberEntity member);

    Integer countCompletedCourseById(Long memberId);

    Integer countCourseById(Long memberId);
}
