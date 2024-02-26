package com.m9d.sroom.common.repository.playlist;

import com.m9d.sroom.common.entity.jpa.PlaylistEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import java.util.*;

@Repository
public class PlaylistJpaRepository {

    private final EntityManager em;

    private final JdbcTemplate jdbcTemplate;

    public PlaylistJpaRepository(EntityManager em, JdbcTemplate jdbcTemplate) {
        this.em = em;
        this.jdbcTemplate = jdbcTemplate;
    }

    public PlaylistEntity save(PlaylistEntity playlist) {
        em.persist(playlist);
        return playlist;
    }

    public PlaylistEntity getById(Long playlistId) {
        try {
            return em.find(PlaylistEntity.class, playlistId);
        } catch (NoResultException e) {
            return null;
        }
    }

    public Optional<PlaylistEntity> findByCode(String playlistCode) {
        return Optional.ofNullable(getByCode(playlistCode));
    }

    public PlaylistEntity getByCode(String playlistCode) {
        try {
            return em.createQuery("select p from PlaylistEntity p where p.playlistCode = :playlistCode",
                            PlaylistEntity.class)
                    .setParameter("playlistCode", playlistCode)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public List<PlaylistEntity> getTopRatedOrder(int limit) {
        return em.createQuery("select p from PlaylistEntity p order by p.review.averageRating desc",
                        PlaylistEntity.class)
                .setMaxResults(limit)
                .getResultList();
    }

    public HashSet<String> getCodeSetByMemberId(Long memberId) {
        return new HashSet<>(jdbcTemplate.query(PlaylistRepositorySql.GET_CODE_SET_BY_MEMBER_ID_QUERY,
                (rs, rowNum) -> rs.getString("playlist_code"), memberId));
    }

    public List<PlaylistEntity> getRandomByChannel(String channel, int limit) {
        List<PlaylistEntity> playlistEntityList = em.createQuery(
                        "select p from PlaylistEntity p where p.contentInfo.channel = :channel", PlaylistEntity.class)
                .setParameter("channel", channel)
                .getResultList();
        Collections.shuffle(playlistEntityList);

        if(playlistEntityList.size() >= limit) {
            return playlistEntityList.subList(0, limit);
        }
        else {
            return playlistEntityList;
        }
    }

    public List<PlaylistEntity> getViewCountOrderByChannel(String channel, int limit) {
        return em.createQuery(
                        "select p from PlaylistEntity p join p.playlistVideoEntityList pv join pv.video v " +
                                "where p.contentInfo.channel = :channel group by p.playlistId " +
                                "order by sum(v.viewCount) desc ", PlaylistEntity.class)
                .setParameter("channel", channel)
                .setMaxResults(limit)
                .getResultList();
    }

    public List<PlaylistEntity> getLatestOrderByChannel(String channel, int limit) {
        return em.createQuery("select p from PlaylistEntity p where p.contentInfo.channel = :channel " +
                        "order by p.contentInfo.publishedAt desc ", PlaylistEntity.class)
                .setParameter("channel", channel)
                .setMaxResults(limit)
                .getResultList();
    }

    public int updateRating() {
        return em.createQuery("update PlaylistEntity p set p.review.averageRating = p.review.accumulatedRating / " +
                "p.review.reviewCount where p.review.reviewCount > 0").executeUpdate();
    }
}
