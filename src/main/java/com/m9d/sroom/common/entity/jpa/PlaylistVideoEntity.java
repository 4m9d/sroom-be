package com.m9d.sroom.common.entity.jpa;

import javax.persistence.*;

@Entity
@Table(name = "PLAYLISTVIDEO")
public class PlaylistVideoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long playlistVideoId;

    @ManyToOne
    @JoinColumn(name = "playlist_id", nullable = false)
    private PlaylistEntity playlist;

    @ManyToOne
    @JoinColumn(name = "video_id", nullable = false)
    private VideoEntity video;

    @Column(nullable = false)
    private Integer videoIndex;
}
