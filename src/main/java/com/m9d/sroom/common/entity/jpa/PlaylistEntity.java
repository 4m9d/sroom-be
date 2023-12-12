package com.m9d.sroom.common.entity.jpa;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name="PLAYLIST")
public class PlaylistEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long playlistId;

    @Column(nullable = false, unique = true)
    private String playlistCode;

    @Column(nullable = false)
    private String channel;

    @Column(nullable = false)
    private String title;

    @Column(nullable = true)
    private Integer duration;

    @Column(nullable = true)
    private String description;

    @Column(nullable = false)
    private Timestamp publishedAt;

    @Column(nullable = false)
    private Integer videoCount;

    @Column(nullable = false)
    @ColumnDefault("0")
    private Integer accumulatedRating;

    @Column(nullable = true)
    @ColumnDefault("false")
    private Boolean available;

    @Column(nullable = false)
    @ColumnDefault("0")
    private Integer reviewCount;

    @Column(nullable = false)
    private String thumbnail;

    @Column(nullable = false)
    @CreationTimestamp
    @UpdateTimestamp
    private Timestamp updatedAt;

    @Column(nullable = false)
    @ColumnDefault("0")
    private Float averageRating;
}
