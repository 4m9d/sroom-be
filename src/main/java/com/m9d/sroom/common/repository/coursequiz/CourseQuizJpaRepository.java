package com.m9d.sroom.common.repository.coursequiz;

import com.m9d.sroom.common.entity.jpa.CourseQuizEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.Optional;

@Repository
public class CourseQuizJpaRepository {

    @PersistenceContext
    EntityManager em;

    public CourseQuizEntity save(CourseQuizEntity courseQuiz) {
        em.persist(courseQuiz);
        return courseQuiz;
    }

    public CourseQuizEntity getById(Long courseQuizId) {
        try {
            return em.find(CourseQuizEntity.class, courseQuizId);
        } catch (NoResultException e) {
            return null;
        }
    }

    public Optional<CourseQuizEntity> findById(Long courseQuizId) {
        return Optional.ofNullable(getById(courseQuizId));
    }

    public void deleteByCourseId(Long courseId) {
        for (CourseQuizEntity courseQuiz : findListByCourseId(courseId)) {
            em.remove(courseQuiz);
        }
    }

    public List<CourseQuizEntity> findListByCourseId(Long courseId) {
        return em.createQuery("select cq from CourseQuizEntity cq where cq.course.courseId = :courseId",
                        CourseQuizEntity.class)
                .setParameter("courseId", courseId)
                .getResultList();
    }
}
