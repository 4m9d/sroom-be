package com.m9d.sroom.repository.member;

import com.m9d.sroom.global.mapper.Member;

import java.util.Optional;

public interface MemberRepository {

    Member save(Member member);

    Member getById(Long memberId);

    Member getByCode(String memberCode);

    Optional<Member> findByCode(String memberCode);

    Optional<Member> findById(Long memberId);

    Member updateById(Long memberId, Member member);

    void updateRefreshTokenById(Long memberId, String refreshToken);

    void addQuizCountById(Long memberId, int quizCount, int correctCount);

    void addTotalLearningTimeById(Long memberId, int timeToAddInSecond);

    void updateCompletionRateById(Long memberId, int completionRate);

    Integer countCompletedCourseById(Long memberId);

    Integer countCourseById(Long memberId);
}
