package com.m9d.sroom.common.repository.review;

import com.m9d.sroom.common.entity.jpa.ReviewEntity;
import com.m9d.sroom.search.dto.response.ReviewBrief;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.List;

@Repository
public class ReviewJpaRepository{
    @PersistenceContext
    EntityManager em;

    public ReviewEntity save(ReviewEntity review) {
        em.persist(review);
        return review;
    }

    public ReviewEntity getById(Long reviewId) {
        return em.find(ReviewEntity.class, reviewId);
    }

    public List<ReviewEntity> getListByCode(String sourceCode, int reviewOffset, int reviewLimit) {
        return em.createQuery("select r from ReviewEntity r where r.sourceCode = :sourceCode", ReviewEntity.class)
                .setParameter("sourceCode", sourceCode)
                .setMaxResults(reviewLimit)
                .setFirstResult(reviewOffset)
                .getResultList();
    }
}
