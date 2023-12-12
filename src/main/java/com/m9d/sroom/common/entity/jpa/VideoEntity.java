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

    private String videoCode;

    private String title;

    private String channel;

    private String description;

    private int duration;

    private boolean playlist;

    private Long viewCount;

    private Timestamp publishedAt;

    private int accumulatedRating;

    private int reviewCount;

    private String thumbnail;

    private String language;

    private String license;

    @CreationTimestamp
    @UpdateTimestamp
    private Timestamp updatedAt;

    private boolean membership;

    @OneToOne
    @JoinColumn(name = "summary_id")
    private SummaryEntity summary;

    private boolean available;

    private boolean chapterUse;

    private Integer materialStatus;

    private Float averageRating;
}
