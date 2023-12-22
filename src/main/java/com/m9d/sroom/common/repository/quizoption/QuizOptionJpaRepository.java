package com.m9d.sroom.common.repository.quizoption;

import com.m9d.sroom.common.entity.jpa.QuizOptionEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
public class QuizOptionJpaRepository {

    private final EntityManager em;

    public QuizOptionJpaRepository(EntityManager em) {
        this.em = em;
    }

    public QuizOptionEntity save(QuizOptionEntity quizOption) {
        em.persist(quizOption);
        return quizOption;
    }

    public QuizOptionEntity getById(Long quizOptionId) {
        return em.find(QuizOptionEntity.class, quizOptionId);
    }
}
