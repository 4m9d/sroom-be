package com.m9d.sroom.common.repository.quiz;


import com.m9d.sroom.common.entity.jpa.MemberEntity;
import com.m9d.sroom.common.entity.jpa.QuizEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import java.util.List;
import java.util.Optional;

@Repository
public class QuizJpaRepository {

    private final EntityManager em;

    public QuizJpaRepository(EntityManager em) {
        this.em = em;
    }

    QuizEntity save(QuizEntity quiz) {
        em.persist(quiz);
        return quiz;
    }

    List<QuizEntity> getListByVideoId(Long videoId) {
        return em.createQuery("select q from QuizEntity q where q.video.id = :videoId", QuizEntity.class)
                .setParameter("videoId", videoId)
                .getResultList();
    }

    QuizEntity getById(Long quizId) {
        try {
            return em.find(QuizEntity.class, quizId);
        } catch (NoResultException e) {
            return null;
        }
    }

    Optional<QuizEntity> findById(Long quizId) {
        return Optional.ofNullable(getById(quizId));
    }
}
