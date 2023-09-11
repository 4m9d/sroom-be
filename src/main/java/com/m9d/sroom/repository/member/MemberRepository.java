package com.m9d.sroom.repository.member;

import com.m9d.sroom.dashboard.dto.response.DashboardMemberData;

public interface MemberRepository {

    DashboardMemberData getDashboardMemberDataById(Long memberId);

}
