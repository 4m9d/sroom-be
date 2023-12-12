package com.m9d.sroom.common.entity.jpa;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="PLAYLIST")
public class PlaylistEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long playlistId;

    private String playlistCode;

    private String channel;

    private String title;

    private Integer duration;

    private String description;

    private Timestamp publishedAt;

    private Integer videoCount;

    private Integer accumulatedRating;

    private Boolean available;

    private Integer reviewCount;

    private String thumbnail;

    @CreationTimestamp
    @UpdateTimestamp
    private Timestamp updatedAt;

    private Float averageRating;

    @OneToMany(mappedBy = "playlist")
    private List<PlaylistVideoEntity> playlistVideoEntityList = new ArrayList<PlaylistVideoEntity>();
}
