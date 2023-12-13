package com.m9d.sroom.common.entity.jpa;

import javax.persistence.*;

@Entity
@Table(name = "PLAYLISTVIDEO")
public class PlaylistVideoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long playlistVideoId;

    @ManyToOne
    @JoinColumn(name = "playlist_id")
    private PlaylistEntity playlist;

    @ManyToOne
    @JoinColumn(name = "video_id")
    private VideoEntity video;

    private Integer videoIndex;
}
