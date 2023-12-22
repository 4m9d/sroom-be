package com.m9d.sroom.common.repository.summary;

import com.m9d.sroom.common.entity.jpa.MemberEntity;
import com.m9d.sroom.common.entity.jpa.SummaryEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import java.util.Optional;

@Repository
public class SummaryJpaRepository {

    private final EntityManager em;

    public SummaryJpaRepository(EntityManager em) {
        this.em = em;
    }

    public SummaryEntity save(SummaryEntity summary) {
        em.persist(summary);
        return summary;
    }

    public SummaryEntity getById(Long summaryId) {
        try {
            return em.find(SummaryEntity.class, summaryId);
        } catch (NoResultException e) {
            return null;
        }
    }

    public Optional<SummaryEntity> findById(Long summaryId) {
        return Optional.ofNullable(getById(summaryId));
    }
}
