package com.m9d.sroom.common.repository.member;

import com.m9d.sroom.common.entity.jpa.MemberEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Optional;

@Repository
public class MemberJpaRepositoryImpl {

    @PersistenceContext
    EntityManager em;

    public MemberEntity save(MemberEntity member) {
        em.persist(member);
        return member;
    }

    public MemberEntity getById(Long memberId) {
        return em.find(MemberEntity.class, memberId);
    }

    public MemberEntity getByCode(String memberCode) {
        return em.createQuery("select m from MemberEntity m where m.memberCode = :memberCode",
                        MemberEntity.class)
                .setParameter("memberCode", memberCode)
                .getSingleResult();
    }

    public Optional<MemberEntity> findById(Long memberId) {
        return Optional.ofNullable(getById(memberId));
    }

    public Optional<MemberEntity> findByCode(String memberCode) {
        return Optional.ofNullable(getByCode(memberCode));
    }

}
