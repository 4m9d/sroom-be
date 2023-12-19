package com.m9d.sroom.common.repository.coursedailylog;

import com.m9d.sroom.common.entity.jpa.CourseDailyLogEntity;
import com.m9d.sroom.common.entity.jpa.MemberEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import java.util.Optional;

@Repository
public class CourseDailyLogJpaRepository {

    private final EntityManager em;

    public CourseDailyLogJpaRepository(EntityManager em) {
        this.em = em;
    }

    public CourseDailyLogEntity save(CourseDailyLogEntity dailyLog) {
        em.persist(dailyLog);
        return dailyLog;
    }

    public CourseDailyLogEntity getById(Long dailyLogId) {
        try {
            return em.find(CourseDailyLogEntity.class, dailyLogId);
        } catch (NoResultException e) {
            return null;
        }
    }

    public Optional<CourseDailyLogEntity> findById(Long dailyLogId) {
        return Optional.ofNullable(getById(dailyLogId));
    }

}
