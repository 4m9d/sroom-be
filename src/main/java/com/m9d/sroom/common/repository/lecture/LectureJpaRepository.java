package com.m9d.sroom.common.repository.lecture;

import com.m9d.sroom.common.entity.jpa.LectureEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import java.util.List;
import java.util.Optional;

@Repository
public class LectureJpaRepository {

    private final EntityManager em;

    public LectureJpaRepository(EntityManager em) {
        this.em = em;
    }

    public LectureEntity save(LectureEntity lecture) {
        em.persist(lecture);
        return lecture;
    }

    public LectureEntity getById(Long lectureId) {
        try {
            return em.find(LectureEntity.class, lectureId);
        } catch (NoResultException e) {
            return null;
        }
    }

    public Optional<LectureEntity> findById(Long lectureId) {
        return Optional.ofNullable(getById(lectureId));
    }

    public List<LectureEntity> findListByCourseId(Long courseId) {
        return em.createQuery("select l from LectureEntity l where l.course.courseId = :courseId",
                        LectureEntity.class)
                .setParameter("courseId", courseId)
                .getResultList();
    }

    public void deleteByCourseId(Long courseId) {
        for (LectureEntity lecture : findListByCourseId(courseId)) {
            em.remove(lecture);
        }
    }
}
