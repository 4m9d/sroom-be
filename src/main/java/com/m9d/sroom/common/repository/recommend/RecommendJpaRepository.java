package com.m9d.sroom.common.repository.recommend;

import com.m9d.sroom.common.entity.jdbctemplate.RecommendEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class RecommendJpaRepository {

    @PersistenceContext
    EntityManager em;

    List<RecommendEntity> getListByDomain(int domainId) {
        return em.createQuery("select r from RecommendEntity r where r.domain = :domainId", RecommendEntity.class)
                .setParameter("domainId", domainId)
                .getResultList();
    }
}
