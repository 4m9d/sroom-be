package com.m9d.sroom.common.repository.playlistvideo;

import com.m9d.sroom.common.entity.jpa.PlaylistVideoEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;

@Repository
public class PlaylistVideoJpaRepository {

    private final EntityManager em;

    public PlaylistVideoJpaRepository(EntityManager em) {
        this.em = em;
    }

    public PlaylistVideoEntity save(PlaylistVideoEntity playlistVideo) {
        em.persist(playlistVideo);
        return playlistVideo;
    }

    public PlaylistVideoEntity getById(Long playlistVideoId) {
        try {
            return em.find(PlaylistVideoEntity.class, playlistVideoId);
        } catch (NoResultException e) {
            return null;
        }
    }

    public void deleteByPlaylistId(Long playlistId) {
        em.createQuery("delete from PlaylistVideoEntity pv where pv.playlist.playlistId = :playlistId")
                .setParameter("playlistId", playlistId);
    }
}
