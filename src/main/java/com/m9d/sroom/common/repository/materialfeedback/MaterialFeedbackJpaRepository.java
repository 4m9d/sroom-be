package com.m9d.sroom.common.repository.materialfeedback;

import com.m9d.sroom.common.entity.jpa.MaterialFeedbackEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

@Repository
public class MaterialFeedbackJpaRepository {

    @PersistenceContext
    EntityManager em;

    public MaterialFeedbackEntity save(MaterialFeedbackEntity feedback) {
        em.persist(feedback);
        return feedback;
    }

    public MaterialFeedbackEntity getById(Long feedbackId) {
        try {
            return em.find(MaterialFeedbackEntity.class, feedbackId);
        } catch (NoResultException e) {
            return null;
        }
    }
}
