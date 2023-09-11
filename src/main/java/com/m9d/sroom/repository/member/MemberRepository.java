package com.m9d.sroom.repository.member;

import com.m9d.sroom.dashboard.dto.response.DashboardMemberData;
import com.m9d.sroom.material.model.MemberQuizInfo;
import com.m9d.sroom.member.domain.Member;

import java.util.Optional;

public interface MemberRepository {

    void save(Member member);

    Member getByCode(String memberCode);

    Optional<Member> findByCode(String memberCode);

    Optional<Member> findById(Long memberId);

    void updateRefreshTokenById(Long memberId, String refreshToken);

    String getNameById(Long memberId);

    String getRefreshTokenById(Long memberId);

    void addQuizCountById(Long memberId, int quizCount, int correctCount);

    MemberQuizInfo getMemberQuizInfoById(Long memberId);

    void addTotalLearningTimeById(Long memberId, int timeToAddInSecond);

    void updateCompletionRateById(Long memberId, int completionRate);

    DashboardMemberData getDashboardMemberDataById(Long memberId);

}
