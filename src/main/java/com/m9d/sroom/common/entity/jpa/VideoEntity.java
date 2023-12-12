package com.m9d.sroom.common.entity.jpa;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "VIDEO")
public class VideoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long videoId;

    @Column(nullable = false, unique = true)
    private String videoCode;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String channel;

    @Column(nullable = true)
    private String description;

    @Column(nullable = false)
    private int duration;

    @Column(nullable = false)
    private boolean playlist;

    @Column(nullable = true)
    private Long viewCount;

    @Column(nullable = false)
    private Timestamp publishedAt;

    @Column(nullable = false)
    @ColumnDefault("0")
    private int accumulatedRating;

    @Column(nullable = false)
    @ColumnDefault("0")
    private int reviewCount;

    @Column(nullable = false)
    private String thumbnail;

    @Column(nullable = true)
    private String language;

    @Column(nullable = false)
    private String license;

    @Column(nullable = false)
    @CreationTimestamp
    @UpdateTimestamp
    private Timestamp updatedAt;

    @Column(nullable = false)
    @ColumnDefault("0")
    private boolean membership;

    @OneToOne
    @JoinColumn(name = "summary_id", nullable = true)
    private SummaryEntity summary;

    @Column(nullable = false)
    @ColumnDefault("1")
    private boolean available;

    @Column(nullable = true)
    @ColumnDefault("0")
    private boolean chapterUse;

    @Column(nullable = false)
    @ColumnDefault("-2")
    private Integer materialStatus;

    @Column(nullable = false)
    @ColumnDefault("0")
    private Float averageRating;
}
