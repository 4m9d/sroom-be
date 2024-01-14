package com.m9d.sroom.common.entity.jpa;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "PLAYLISTVIDEO")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
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

    public PlaylistVideoEntity(PlaylistEntity playlist, int index, VideoEntity video) {
        this.playlist = playlist;
        this.video = video;
        this.videoIndex = index;
    }

    private PlaylistVideoEntity(PlaylistEntity playlist, VideoEntity video, Integer videoIndex) {
        this.playlist = playlist;
        this.video = video;
        this.videoIndex = videoIndex;
    }

    public static PlaylistVideoEntity create(PlaylistEntity playlist, VideoEntity videoEntity, int index) {
        return new PlaylistVideoEntity(playlist, videoEntity, index);
    }
}
