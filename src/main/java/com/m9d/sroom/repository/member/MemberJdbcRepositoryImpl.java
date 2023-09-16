package com.m9d.sroom.repository.member;

import com.m9d.sroom.global.mapper.Member;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class MemberJdbcRepositoryImpl implements MemberRepository{
    @Override
    public void save(Member member) {

    }

    @Override
    public Member getById(Long memberId) {
        return null;
    }

    @Override
    public Member getByCode(String memberCode) {
        return null;
    }

    @Override
    public Optional<Member> findByCode(String memberCode) {
        return Optional.empty();
    }

    @Override
    public Optional<Member> findById(Long memberId) {
        return Optional.empty();
    }

    @Override
    public void updateRefreshTokenById(Long memberId, String refreshToken) {

    }

    @Override
    public void addQuizCountById(Long memberId, int quizCount, int correctCount) {

    }

    @Override
    public void addTotalLearningTimeById(Long memberId, int timeToAddInSecond) {

    }

    @Override
    public void updateCompletionRateById(Long memberId, int completionRate) {

    }
}
