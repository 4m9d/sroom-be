package com.m9d.sroom.member;

import com.m9d.sroom.common.entity.jpa.MemberEntity;
import com.m9d.sroom.common.repository.member.MemberJpaRepository;
import com.m9d.sroom.util.SroomTest;
import com.m9d.sroom.util.TestConstant;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

public class MemberRepositoryTest extends SroomTest {

    @Autowired
    private MemberJpaRepository memberJpaRepository;

    @Test
    @DisplayName("저장되지 않은 member는 Optional.isEmpty() = true 입니다.")
    void nullIfNotSavedMember() {
        //given
        Optional<MemberEntity> memberEntityOptional = memberJpaRepository.findById(1L);

        //when


        //then
        Assertions.assertEquals(Optional.empty(), memberEntityOptional);
    }

    @Test
    @DisplayName("저장된 member는 조회에 성공합니다.")
    void getMemberIfSaved() {
        MemberEntity memberEntity = MemberEntity.create(TestConstant.MEMBER_PROFILE, TestConstant.MEMBER_CODE);
        memberJpaRepository.save(memberEntity);

        //when
        Optional<MemberEntity> memberEntityOptional = memberJpaRepository.findById(1L);

        //then
        Assertions.assertTrue(memberEntityOptional.isPresent());
        Assertions.assertNotNull(memberEntity.getMemberId());
        Assertions.assertEquals(memberEntityOptional.get(), memberEntity);
    }

    @Test
    @DisplayName("jpa 사용하면 멤버 이름을 수정하기만 해도 db 에 반영됩니다.")
    void updateMemberName() {
        //given
        MemberEntity memberEntity = MemberEntity.create(TestConstant.MEMBER_PROFILE, TestConstant.MEMBER_CODE);
        memberJpaRepository.save(memberEntity);

        //when
        String newName = "수정될 이름임 ㅇㅇ";
        memberEntity.updateName(newName);

        //then
        MemberEntity member = memberJpaRepository.getById(1L);
        Assertions.assertEquals(member.getMemberName(), newName);
    }
}
