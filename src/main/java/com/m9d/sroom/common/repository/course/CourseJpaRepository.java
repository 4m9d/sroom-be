package com.m9d.sroom.common.repository.course;

import com.m9d.sroom.common.entity.jpa.CourseEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import java.util.Optional;

@Repository
public class CourseJpaRepository {

    private final EntityManager em;

    public CourseJpaRepository(EntityManager em) {
        this.em = em;
    }

    public CourseEntity save(CourseEntity course) {
        em.persist(course);
        return course;
    }

    public CourseEntity getById(Long courseId) {
        try {
            return em.find(CourseEntity.class, courseId);
        } catch (NoResultException e) {
            return null;
        }
    }

    public Optional<CourseEntity> findById(Long courseId) {
        return Optional.ofNullable(getById(courseId));
    }

    public void deleteById(Long courseId) {
        CourseEntity course = getById(courseId);
        course.getMember().getCourses().remove(course);

        em.remove(course);
    }
}
