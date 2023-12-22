package com.m9d.sroom.common.repository.member;

import com.m9d.sroom.common.entity.jpa.MemberEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import java.util.Optional;

@Repository
public class MemberJpaRepository {

    private final EntityManager em;

    public MemberJpaRepository(EntityManager em) {
        this.em = em;
    }

    public MemberEntity save(MemberEntity member) {
        em.persist(member);
        return member;
    }

    public MemberEntity getById(Long memberId) {
        try {
            return em.find(MemberEntity.class, memberId);
        } catch (NoResultException e) {
            return null;
        }
    }

    public MemberEntity getByCode(String memberCode) {
        try {
            return em.createQuery("select m from MemberEntity m where m.memberCode = :memberCode",
                            MemberEntity.class)
                    .setParameter("memberCode", memberCode)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public Optional<MemberEntity> findById(Long memberId) {
        return Optional.ofNullable(getById(memberId));
    }

    public Optional<MemberEntity> findByCode(String memberCode) {
        return Optional.ofNullable(getByCode(memberCode));
    }

}
