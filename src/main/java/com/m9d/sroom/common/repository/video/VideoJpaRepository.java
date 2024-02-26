package com.m9d.sroom.common.repository.video;

import com.m9d.sroom.common.entity.jpa.VideoEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Repository
public class VideoJpaRepository {

    private final EntityManager em;

    public VideoJpaRepository(EntityManager entityManager) {
        this.em = entityManager;
    }

    public VideoEntity save(VideoEntity video) {
        em.persist(video);
        return video;
    }

    public VideoEntity getByCode(String videoCode) {
        try {
            return em.createQuery("select v from VideoEntity v where v.videoCode = :videoCode",
                            VideoEntity.class)
                    .setParameter("videoCode", videoCode)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public Optional<VideoEntity> findByCode(String videoCode) {
        return Optional.ofNullable(getByCode(videoCode));
    }

    public VideoEntity getById(Long videoId) {
        try {
            return em.find(VideoEntity.class, videoId);
        } catch (NoResultException e) {
            return null;
        }
    }

    public Optional<VideoEntity> findById(Long videoId) {
        return Optional.ofNullable(getById(videoId));
    }

    public List<VideoEntity> getTopRatedOrder(int limit) {
        return em.createQuery("select v from VideoEntity v order by v.review.averageRating desc",
                        VideoEntity.class)
                .setMaxResults(limit)
                .getResultList();
    }

    public List<VideoEntity> getRandomByChannel(String channel, int limit) {
        List<VideoEntity> videoEntityList = em.createQuery(
                        "select v from VideoEntity v where v.contentInfo.channel = :channel", VideoEntity.class)
                .setParameter("channel", channel)
                .getResultList();
        Collections.shuffle(videoEntityList);

        if (videoEntityList.size() >= limit) {
            return videoEntityList.subList(0, limit);
        }
        else {
            return videoEntityList;
        }
    }

    public List<VideoEntity> getViewCountOrderByChannel(String channel, int limit) {
        return em.createQuery(
                        "select v from VideoEntity v where v.contentInfo.channel = :channel " +
                                "order by v.viewCount desc", VideoEntity.class)
                .setParameter("channel", channel)
                .setMaxResults(limit)
                .getResultList();
    }

    public List<VideoEntity> getLatestOrderByChannel(String channel, int limit) {
        return em.createQuery("select v from VideoEntity v where v.contentInfo.channel = :channel " +
                        "order by v.contentInfo.publishedAt desc ", VideoEntity.class)
                .setParameter("channel", channel)
                .setMaxResults(limit)
                .getResultList();
    }

    public int updateRating() {
        return em.createQuery("update VideoEntity v set v.review.averageRating = v.review.accumulatedRating / " +
                "v.review.reviewCount where v.review.reviewCount > 0").executeUpdate();
    }
}
