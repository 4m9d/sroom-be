package com.m9d.sroom.common.repository.coursevideo;

import com.m9d.sroom.common.entity.jpa.CourseVideoEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import java.util.List;
import java.util.Optional;

@Repository
public class CourseVideoJpaRepository {

    private final EntityManager em;

    public CourseVideoJpaRepository(EntityManager em) {
        this.em = em;
    }

    public CourseVideoEntity save(CourseVideoEntity courseVideo) {
        em.persist(courseVideo);
        return courseVideo;
    }

    public CourseVideoEntity getById(Long courseVideoId) {
        try {
            return em.find(CourseVideoEntity.class, courseVideoId);
        } catch (NoResultException e) {
            return null;
        }
    }

    public Optional<CourseVideoEntity> findById(Long courseVideoId) {
        return Optional.ofNullable(getById(courseVideoId));
    }

    public void deleteByCourseId(Long courseId) {
        for (CourseVideoEntity courseVideo : findListByCourseId(courseId)) {
            em.remove(courseVideo);
        }
    }

    public List<CourseVideoEntity> findListByCourseId(Long courseId) {
        return em.createQuery("select cv from CourseVideoEntity cv where cv.course.courseId = :courseId",
                        CourseVideoEntity.class)
                .setParameter("courseId", courseId)
                .getResultList();
    }

    public List<CourseVideoEntity> findListByVideoId(Long videoId) {
        return em.createQuery("select cv from CourseVideoEntity cv where cv.video.videoId = :videoId",
                        CourseVideoEntity.class)
                .setParameter("videoId", videoId)
                .getResultList();
    }
}
